package com.example.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.grpc.client.ImportGrpcClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.grpc.account.AccountServiceGrpc;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@ImportGrpcClients(target = "account", types = AccountServiceGrpc.AccountServiceBlockingStub.class)
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

}
