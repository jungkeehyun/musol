package com.mims.wake.netty.configuration;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mims.wake.netty.handler.JsonHandler;
import com.mims.wake.netty.initializer.WebSocketChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Configuration
public class ServerConfiguration {
	
	private static Logger logger = LoggerFactory.getLogger(WebSocketChannelInitializer.class);
	
	@Value("${netty.server.transfer.type}")
	private String transferType;        // Server Transfer Type : TCP / UDP / UDT
	@Value("${netty.server.transfer.port}")
	private int transferPort;           // Server Transfer Port : TCP PORT
	@Value("${netty.server.thread.count.boss}")
	private int threadCountBoss;        // Netty Server Boss Thread Count
	@Value("${netty.server.thread.count.worker}")
	private int threadCountWorker;      // Netty Server Worker Thread Count
	@Value("${netty.server.log.level.bootstrap}")
	private String logLevelBootstrap;   // ServerBootStrap Log Level
	
	@Autowired
	private WebSocketChannelInitializer webSocketChannelInitializer;

	/**
	 * Netty Server Boss Thread Setting
	 *
	 * @return
	 */
	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup bossGroup() {
		switch (transferType) {
			case "websocket":
			case "tcp":
			default:
				return new NioEventLoopGroup(threadCountBoss);
		}
	}

	/**
	 * Netty Server Worker Thread Setting
	 *
	 * @return
	 */
	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup workerGroup() {
		switch (transferType) {
			case "websocket":
			case "tcp":
			default:
				return new NioEventLoopGroup(threadCountWorker);
		}
	}

	/**
	 * Transfer Port Setting
	 * @return
	 */
	@Bean
	public InetSocketAddress port() {
		return new InetSocketAddress(transferPort);
	}

	/**
	 * Netty ServerBootStrap Setting
	 *   - LogLevel Setting
     *   - User input process (Handler) 
     *     Netty.Server.Initializer.NettyChannelInitializer
	 *   - Transfer Type channel 
	 * @return
	 */
	@Bean
	public ServerBootstrap serverBootstrap() {
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		
		serverBootstrap
				.group(bossGroup(), workerGroup())
				.handler(new LoggingHandler(LogLevel.valueOf(logLevelBootstrap)))
				.childHandler(webSocketChannelInitializer);

		logger.info("■□■□■ serverBootstrap TransferType : {}", transferType);
		
		switch (transferType) {
			case "websocket":
				//logger.info("@@ serverBootstrap WEBSOCKET");
				
			case "tcp":
				//logger.info("@@ serverBootstrap TCP");
				
			default:
				logger.info("@@ serverBootstrap Channel NioServerSocketChannel");
				serverBootstrap.channel(NioServerSocketChannel.class);
		}

		return serverBootstrap;
	}

	/**
	 * Handler Bean Setting
	 * Netty.Server.Initializer.NettyChannelInitializer
	 *
	 * @return
	 */
	@Bean
	public ChannelInboundHandlerAdapter handler() {
		return new JsonHandler();
	}
}
