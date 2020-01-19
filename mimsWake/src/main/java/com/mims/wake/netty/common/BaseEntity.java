package com.mims.wake.netty.common;

import io.netty.buffer.ByteBuf;

public abstract class BaseEntity<T> {

	// === abstract
	public abstract ByteBuf formatToByteBuf() throws Exception;
}
