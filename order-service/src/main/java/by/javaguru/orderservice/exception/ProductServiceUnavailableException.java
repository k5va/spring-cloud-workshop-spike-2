package by.javaguru.orderservice.exception;

public class ProductServiceUnavailableException extends RuntimeException {

    public ProductServiceUnavailableException(Throwable cause) {
        super("product-service is unavailable", cause);
    }
}
