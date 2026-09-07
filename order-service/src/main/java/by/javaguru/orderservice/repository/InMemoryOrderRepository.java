package by.javaguru.orderservice.repository;

import by.javaguru.orderservice.model.Order;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public List<Order> findAll() {
        return List.copyOf(orders.values());
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public Order save(Long productId, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        long id = idSequence.incrementAndGet();
        Order order = new Order(id, productId, quantity, unitPrice, totalPrice);
        orders.put(id, order);
        return order;
    }
}
