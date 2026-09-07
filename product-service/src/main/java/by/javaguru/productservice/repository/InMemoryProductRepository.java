package by.javaguru.productservice.repository;

import by.javaguru.productservice.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @PostConstruct
    void seed() {
        save("Laptop", new BigDecimal("1200.00"));
        save("Mouse", new BigDecimal("25.50"));
        save("Keyboard", new BigDecimal("45.00"));
        save("Monitor", new BigDecimal("300.00"));
        save("Headphones", new BigDecimal("89.99"));
    }

    private void save(String name, BigDecimal price) {
        long id = idSequence.incrementAndGet();
        products.put(id, new Product(id, name, price));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }
}
