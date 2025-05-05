package com.collabedit.collabserver;

import org.springframework.boot.SpringApplication;//used to launch/run your app
import org.springframework.boot.autoconfigure.SpringBootApplication;// tells Spring Boot to scan this class and its package for components, and automatically configures everything needed to launch the app.”

@SpringBootApplication
public class CollabserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollabserverApplication.class, args);
	}
// “Start the app from this class and look for @Component, @Service, etc.”

}
