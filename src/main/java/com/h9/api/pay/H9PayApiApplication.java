package com.h9.api.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({ "com.h9.api.pay" })
@EnableJpaRepositories(basePackages = "com.h9.api.pay.db.repository")
@EntityScan(basePackages = "com.h9.api.pay.db.entity")
@EnableAutoConfiguration(exclude = JpaRepositoriesAutoConfiguration.class)
public class  H9PayApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(H9PayApiApplication.class, args);
	}
}
