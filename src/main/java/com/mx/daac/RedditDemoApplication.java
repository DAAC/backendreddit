package com.mx.daac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RedditDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedditDemoApplication.class, args);
	}

}
