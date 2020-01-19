package com.mims.wake.netty;

import java.net.InetSocketAddress;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;

/**
 * Web Socket Server
 * @author GenieInBed
 */
@Component
public class WebSocketServer {
	
	private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

	@Autowired
	private ServerBootstrap serverBootstrap;
	@Autowired
	private InetSocketAddress port;
	
	private Channel channel;

	/**
	 * Netty Server Start
	 * Using : ServerBootStrap, port Bean, NettyServerConfiguration
	 *
	 * @throws Exception
	 */
	public void startServer() throws Exception {
		logger.info("■□■□■ Server START");
		channel = serverBootstrap.bind(port).sync().channel().closeFuture().sync().channel();
	}

	@PreDestroy
	public void stopServer() throws Exception {
		logger.info("■□■□■ Server STOP (auto PreDestroy)");
		
		channel.close();
		channel.parent().close();
	}
	
}
