package com.ssafy.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan("com.ssafy")
public class DebugTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(DebugTestApplication.class, args);
	}

}
