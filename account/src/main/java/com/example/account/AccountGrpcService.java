package com.example.account;

import org.springframework.grpc.server.service.GrpcService;

import com.example.grpc.account.AccountServiceGrpc;
import com.example.grpc.account.RefundRequest;
import com.example.grpc.account.RefundResponse;
import com.example.grpc.account.WithdrawRequest;
import com.example.grpc.account.WithdrawResponse;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@GrpcService
@RequiredArgsConstructor
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

	private final AccountService accountService;

	@Override
	public void withdraw(WithdrawRequest request, StreamObserver<WithdrawResponse> observer) {
		accountService.withdraw(request.getAccountId(), request.getAmount());
		WithdrawResponse response = WithdrawResponse.getDefaultInstance();
		observer.onNext(response);
		observer.onCompleted();
	}

	@Override
	public void refund(RefundRequest request, StreamObserver<RefundResponse> observer) {
		accountService.refund(request.getAccountId(), request.getAmount());
		RefundResponse response = RefundResponse.getDefaultInstance();
		observer.onNext(response);
		observer.onCompleted();
	}

}
