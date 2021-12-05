package com.shau.mocap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.shau")
public class MocapApplication {

	public static void main(String[] args) {
		SpringApplication.run(MocapApplication.class, args);
	}

}
