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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderSaga orderSaga;
	private final OrderService orderService;

	@PostMapping
	public Long create(@RequestBody OrderRequest request) {
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

	public record OrderRequest(long productId, int quantity, long accountId, String address) {
	}

	public record OrderResponse(Long orderId, OrderStatus status) {
	}

}
