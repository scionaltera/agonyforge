package com.agonyforge.mud.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.agonyforge.mud.core")
@ComponentScan("com.agonyforge.mud.models.dynamodb")
@ComponentScan("com.agonyforge.mud.demo")
public class MudApplication {
	public static void main(String[] args) {
		SpringApplication.run(MudApplication.class, args);
	}
}
