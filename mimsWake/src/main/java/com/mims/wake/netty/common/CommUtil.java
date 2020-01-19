package com.mims.wake.netty.common;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommUtil {
	private static Logger logger = LoggerFactory.getLogger(CommUtil.class);

	public static void runSafely(SafeRunnable r){
		try {
			r.run();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static interface SafeRunnable {
		public void run() throws Exception;
	}

	public static String toString(StackTraceElement[] arr){
		if(arr==null){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Arrays.asList(arr).stream().skip(1).forEach(t->sb.append(t.toString()).append("\n"));
		return sb.toString();
	}

	public static void sleep(int seconds){
		try {
			Thread.sleep(seconds*1000);
		} catch (InterruptedException e) {
			logger.warn("[ignore]",  e);
		}
	}
}
