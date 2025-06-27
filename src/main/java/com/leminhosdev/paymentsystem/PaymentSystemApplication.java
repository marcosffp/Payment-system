package com.leminhosdev.paymentsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.github.cdimascio.dotenv.Dotenv;

@PropertySource("classpath:application-dev.properties")
@SpringBootApplication
public class PaymentSystemApplication {

	public static void main(String[] args) {
		// Carregar variáveis do .env
		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USER"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("MAIL_USER", dotenv.get("MAIL_USER"));
		System.setProperty("MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET")); 
		SpringApplication.run(PaymentSystemApplication.class, args);
	}
}
