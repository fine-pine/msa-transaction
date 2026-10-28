package com.example.inventory;

import org.springframework.stereotype.Component;

import com.example.grpc.account.AccountServiceGrpc;
import com.example.grpc.account.RefundRequest;
import com.example.grpc.account.WithdrawRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountClient {

	private final AccountServiceGrpc.AccountServiceBlockingStub stub;

	public void withdraw(Long orderId, Long accountId, long amount) {
		WithdrawRequest request = WithdrawRequest.newBuilder()
				.setOrderId(orderId)
				.setAccountId(accountId)
				.setAmount(amount)
				.build();
		stub.withdraw(request);
	}

	public void refund(Long orderId, Long accountId, long amount) {
		RefundRequest request = RefundRequest.newBuilder()
				.setOrderId(orderId)
				.setAccountId(accountId)
				.setAmount(amount)
				.build();
		stub.refund(request);
	}

}
