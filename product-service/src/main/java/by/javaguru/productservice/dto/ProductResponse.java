package by.javaguru.productservice.dto;

import by.javaguru.productservice.model.Product;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, BigDecimal price) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.id(), product.name(), product.price());
    }
}
