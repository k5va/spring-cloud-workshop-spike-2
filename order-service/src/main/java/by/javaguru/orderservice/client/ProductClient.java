package by.javaguru.orderservice.client;

import by.javaguru.orderservice.client.dto.ProductDto;

public interface ProductClient {

    /**
     * @throws by.javaguru.orderservice.exception.ProductNotFoundException if product-service returns 404
     * @throws by.javaguru.orderservice.exception.ProductServiceUnavailableException on connection failure, timeout or 5xx
     */
    ProductDto getProduct(Long productId);
}
