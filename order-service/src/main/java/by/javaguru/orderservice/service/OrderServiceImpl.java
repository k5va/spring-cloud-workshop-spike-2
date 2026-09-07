package by.javaguru.orderservice.service;

import by.javaguru.orderservice.client.ProductClient;
import by.javaguru.orderservice.client.dto.ProductDto;
import by.javaguru.orderservice.dto.CreateOrderRequest;
import by.javaguru.orderservice.exception.OrderNotFoundException;
import by.javaguru.orderservice.model.Order;
import by.javaguru.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public Order createOrder(CreateOrderRequest request) {
        ProductDto product = productClient.getProduct(request.productId());
        BigDecimal unitPrice = product.price();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.quantity()));
        return orderRepository.save(request.productId(), request.quantity(), unitPrice, totalPrice);
    }
}
