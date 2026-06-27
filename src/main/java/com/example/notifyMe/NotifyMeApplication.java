package com.example.notifyMe;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@RequiredArgsConstructor
public class NotifyMeApplication implements CommandLineRunner {
	private final PdfMonitorService service;

	public static void main(String[] args) {
		SpringApplication.run(NotifyMeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		service.check();
		System.exit(0);
	}
}
