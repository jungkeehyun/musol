package com.mims.wake.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mims.wake.service.LoginService;
import com.mims.wake.service.MessageService;

import io.netty.channel.Channel;

@Component
public class UserIdChannelRepository {

	@Autowired
	private LoginService loginService;
	@Autowired
	private MessageService messageService;

	private final Map<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();

	public Map<String, Channel> getUserIdChannelMap() {
		return userIdChannelMap;
	}

	public void writeAndFlush(String returnMessage) throws Exception {
		userIdChannelMap.values().parallelStream().forEach(channel -> {

			if (!channel.isActive()) {
				loginService.removeUser(channel);
				channel.close();
				return;
			}

			channel.writeAndFlush(messageService.returnMessage(returnMessage));
		});
	}

}
