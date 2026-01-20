package com.a2z;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;



@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
@CrossOrigin("http://localhost:4200") 
@EnableTransactionManagement
public class A2zApplication {

	/*
	 * @CrossOrigin( origins = { "http://localhost:4200" }, methods = {
	 * RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.PUT,
	 * RequestMethod.DELETE, RequestMethod.POST })
	 */
	
	public static void main(String[] args) {
		SpringApplication.run(A2zApplication.class, args);
	}
}
