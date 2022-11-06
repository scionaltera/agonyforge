package com.agonyforge.mud.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.agonyforge.mud.core")
@ComponentScan("com.agonyforge.mud.demo")
public class MudApplication {
	public static void main(String[] args) {
		SpringApplication.run(MudApplication.class, args);
	}
}
