package by.javaguru.productservice.repository;

import by.javaguru.productservice.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(Long id);
}
