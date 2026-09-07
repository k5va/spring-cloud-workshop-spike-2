package by.javaguru.orderservice.dto;

import by.javaguru.orderservice.model.Order;

import java.math.BigDecimal;

public record OrderResponse(Long id, Long productId, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.id(), order.productId(), order.quantity(), order.unitPrice(), order.totalPrice());
    }
}
