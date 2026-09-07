package by.javaguru.orderservice.service;

import by.javaguru.orderservice.dto.CreateOrderRequest;
import by.javaguru.orderservice.model.Order;

import java.util.List;

public interface OrderService {

    List<Order> getAllOrders();

    Order getOrderById(Long id);

    Order createOrder(CreateOrderRequest request);
}
