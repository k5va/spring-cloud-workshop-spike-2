package by.javaguru.productservice.service;

import by.javaguru.productservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);
}
