package com.mims.wake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mims.wake.netty.WebSocketServer;

/**
 * Spring-Boot Start (with Main)
 * @author GenieInBed
 */
@SpringBootApplication
public class MimsWakeApplication implements CommandLineRunner  {

	private static final Logger logger = LoggerFactory.getLogger(MimsWakeApplication.class);

	@Autowired
	WebSocketServer webSocketServer;

	/**
	 * MIMS 항적 프로그램(Spring Boot) 시작.
	 * run() 에서 실제 프로그램 시작
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		logger.info("■□■□■MIM Wake Websocket Server Start■□■□■");
		SpringApplication.run(MimsWakeApplication.class, args);
		
		/*
		ApplicationContext ctx = SpringApplication.run(MimsWakeApplication.class, args);
        WebSocketServer ws = ctx.getBean(WebSocketServer.class);
        ws.startServer();
        */
	}
	
	/**
	 * CommandLineRunner 에 의해 자동 실행
	 *
	 * @param strings
	 * @throws Exception
	 */
	@Override
	public void run(String... strings) throws Exception {

		// Websocket Server START!!
		webSocketServer.startServer();

	}
	
}