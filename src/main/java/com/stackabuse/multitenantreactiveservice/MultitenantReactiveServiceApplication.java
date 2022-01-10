package com.stackabuse.multitenantreactiveservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;

@SpringBootApplication(exclude = {
		MongoReactiveAutoConfiguration.class,
		MongoReactiveDataAutoConfiguration.class,
		MongoAutoConfiguration.class
})
public class MultitenantReactiveServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MultitenantReactiveServiceApplication.class, args);
	}
}
