package by.javaguru.orderservice.service;

import by.javaguru.orderservice.dto.CreateOrderRequest;
import by.javaguru.orderservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long id);

    OrderResponse createOrder(CreateOrderRequest request);
}
