package com.example.inventory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderSaga orderSaga;
	private final OrderService orderService;

	@PostMapping
	public Long create(@Valid @RequestBody OrderRequest request) {
		return orderSaga.start(request.productId(), request.quantity(), request.accountId(), request.address());
	}

	@GetMapping("/{orderId}")
	public OrderResponse get(@PathVariable Long orderId) {
		Order order = orderService.get(orderId);
		return new OrderResponse(order.getId(), order.getStatus());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ProblemDetail handle(IllegalArgumentException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	public ProblemDetail handle(IllegalStateException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler(ArithmeticException.class)
	public ProblemDetail handle(ArithmeticException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "주문 금액이 허용 범위를 넘었습니다.");
	}

	public record OrderRequest(
			@NotNull Long productId,
			@NotNull @Positive Integer quantity,
			@NotNull Long accountId,
			@NotBlank @Size(max = 255) String address) {
	}

	public record OrderResponse(Long orderId, OrderStatus status) {
	}

}
