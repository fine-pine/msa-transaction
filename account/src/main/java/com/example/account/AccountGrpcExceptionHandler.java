package com.example.account;

import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

import io.grpc.Status;
import io.grpc.StatusException;

@Component
public class AccountGrpcExceptionHandler implements GrpcExceptionHandler {

	@Override
	public StatusException handleException(Throwable exception) {
		return switch (exception) {
			case IllegalArgumentException e -> Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asException();
			case IllegalStateException e -> Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asException();
			default -> null;
		};
	}

}
