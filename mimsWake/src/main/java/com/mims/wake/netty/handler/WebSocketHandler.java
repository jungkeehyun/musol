package com.mims.wake.netty.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mims.wake.service.MessageService;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Web Socket Handler
 * @author GenieInBed
 */
@Component
@Qualifier("websocketHandler")
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MessageService messageService;
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
    	logger.info("■□■□■ websocketHandler channelRead0");
    	
    	//handleWebSocketFrame(ctx, frame);
    	
		Map<String, Object> result = new HashMap<>();

		// 접속자 채널 정보(연결 정보)
		Channel channel = ctx.channel();

		if (!(frame instanceof TextWebSocketFrame))
			throw new UnsupportedOperationException("unsupported frame type : " + frame.getClass().getName());

		// 전송된 내용을 JSON 개체로 변환
		Map<String, Object> data;
		try {
			data = objectMapper.readValue(((TextWebSocketFrame) frame).text(), new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonParseException | JsonMappingException e) {
			e.printStackTrace();
			messageService.returnMessage(channel, result, e, "1001");
			return;
		}

		messageService.execute(channel, data, result);
		
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	logger.info("■□■□■ websocketHandler channel Active");
        
        // Add to channelGroup channel group
        //ChannelHandlerPoollll.channelGroup.add(ctx.channel());
    }

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("■□■□■ websocketHandler channel Inactive");
		
		// Remove to channelGroup channel group
        //ChannelHandlerPoollll.channelGroup.remove(ctx.channel());
	}

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // Used to trigger user events, including triggering read idle, write idle, read and write idle
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                Channel channel = ctx.channel();
                // Close useless channels to prevent waste of resources
                channel.close();
            }
        }
    }

    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}
    
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            //pojoEndpointServer.doOnMessage(ctx, frame);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof CloseWebSocketFrame) {
            ctx.writeAndFlush(frame.retainedDuplicate()).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            //pojoEndpointServer.doOnBinary(ctx, frame);
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            return;
        }
    }	

}
