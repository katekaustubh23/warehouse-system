package com.userservice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import java.util.TimeZone;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class }, scanBasePackages = {"com.userservice","com.authlib"})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);

	}
	@PostConstruct
	public void init() {
		// Force JVM default timezone
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		System.out.println("Default JVM TimeZone set to: " + TimeZone.getDefault().getID());
	}

}
