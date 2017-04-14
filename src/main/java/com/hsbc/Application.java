package com.hsbc;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hsbc.payment.PaymentService;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) throws BeansException, IOException {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		context.getBean(PaymentService.class).start();
	}
}
