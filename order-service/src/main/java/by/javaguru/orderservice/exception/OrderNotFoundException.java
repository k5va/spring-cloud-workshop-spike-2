package by.javaguru.orderservice.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("Order with id %d not found".formatted(orderId));
    }
}
