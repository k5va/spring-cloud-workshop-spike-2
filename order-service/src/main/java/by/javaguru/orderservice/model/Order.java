package by.javaguru.orderservice.model;

import java.math.BigDecimal;

public record Order(Long id, Long productId, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
}
