package com.example.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.grpc.client.ImportGrpcClients;

import com.example.grpc.account.AccountServiceGrpc;

@SpringBootApplication
@ImportGrpcClients(target = "account", types = AccountServiceGrpc.AccountServiceBlockingStub.class)
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

}
