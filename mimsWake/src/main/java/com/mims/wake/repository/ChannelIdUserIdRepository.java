package com.mims.wake.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.netty.channel.ChannelId;

@Component
public class ChannelIdUserIdRepository {

	private final Map<ChannelId, String> channelIdUserIdMap = new ConcurrentHashMap<>();

	public Map<ChannelId, String> getChannelIdUserIdMap() {
		return channelIdUserIdMap;
	}

}
