package by.javaguru.productservice.service;

import by.javaguru.productservice.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();

    Product getProductById(Long id);
}
