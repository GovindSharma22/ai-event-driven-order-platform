package com.example.ai_event_driven_order_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AiEventDrivenOrderPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiEventDrivenOrderPlatformApplication.class, args);
	}

}
