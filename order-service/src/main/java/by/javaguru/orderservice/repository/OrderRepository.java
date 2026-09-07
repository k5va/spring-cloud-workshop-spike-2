package by.javaguru.orderservice.repository;

import by.javaguru.orderservice.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    List<Order> findAll();

    Optional<Order> findById(Long id);

    Order save(Long productId, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice);
}
