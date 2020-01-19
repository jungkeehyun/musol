package com.mims.wake.netty.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

/**
 * Web Socket Server Initializer
 * @author GenieInBed
 */
@Component
@Qualifier("webSocketChannelInitializer")
public class WebSocketChannelInitializer extends ChannelInitializer<Channel>  {

	private static Logger logger = LoggerFactory.getLogger(WebSocketChannelInitializer.class);
	
	private static final StringDecoder STRING_DECODER = new StringDecoder(CharsetUtil.UTF_8);
	private static final StringEncoder STRING_ENCODER = new StringEncoder(CharsetUtil.UTF_8);

	@Value("${netty.server.transfer.type}")
	private String transferType;
	@Value("${netty.server.transfer.maxContentLength}")
	private int transferMaxContentLength;
	@Value("${netty.server.transfer.websocket.path}")
	private String transferWebsocketPath;
	@Value("${netty.server.transfer.websocket.subProtocol}")
	private String transferWebsocketSubProtocol;
	@Value("${netty.server.transfer.websocket.allowExtensions}")
	private boolean transferWebsocketAllowExtensions;
	@Value("${netty.server.transfer.websocket.maxFrameSize}")
	private int maxFrameSize;
	@Value("${netty.server.log.level.pipeline}")
	private String logLevelPipeline;
	
	@Autowired
	@Qualifier("websocketHandler")
	private ChannelInboundHandlerAdapter websocketHandler;
	@Autowired
	@Qualifier("jsonHandler")
	private ChannelInboundHandlerAdapter jsonHandler;
	
	/**
	 * Channel Pipeline Setting
	 * @param channel
	 * @throws Exception
	 */
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		logger.info("■□■□■ TransferType : {}", transferType);
		logger.info("■□■□■ transferWebsocketPath : {}", transferWebsocketPath);
		logger.info("■□■□■ transferWebsocketSubProtocol : {}", transferWebsocketSubProtocol);
		logger.info("■□■□■ transferWebsocketAllowExtensions : {}", transferWebsocketAllowExtensions);

		switch (transferType) {
			case "websocket":
				logger.info("initChannel WebsocketHandler pipeline Setting!!");
				pipeline
						.addLast(new HttpServerCodec())
						.addLast(new HttpObjectAggregator(65536))
						//.addLast(new WebSocketServerCompressionHandler())
						.addLast(new ChunkedWriteHandler())
						//.addLast(new WebSocketServerProtocolHandler(transferWebsocketPath, transferWebsocketSubProtocol, transferWebsocketAllowExtensions))
						.addLast(new WebSocketServerProtocolHandler(transferWebsocketPath, transferWebsocketSubProtocol, transferWebsocketAllowExtensions, maxFrameSize))
						.addLast(new LoggingHandler(LogLevel.valueOf(logLevelPipeline)))
						.addLast(websocketHandler);

			case "tcp":
				
			default:
				logger.info("initChannel JsonHandler pipeline Setting!!");
				pipeline
						.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE))
						.addLast(STRING_DECODER)
						.addLast(STRING_ENCODER)
						.addLast(new LoggingHandler(LogLevel.valueOf(logLevelPipeline)))
						.addLast(jsonHandler);

		}

	}
	
}
