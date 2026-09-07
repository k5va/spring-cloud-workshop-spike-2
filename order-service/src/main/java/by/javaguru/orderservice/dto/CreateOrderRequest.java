package by.javaguru.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(

        @NotNull(message = "must not be null")
        Long productId,

        @NotNull(message = "must not be null")
        @Positive(message = "must be greater than 0")
        Integer quantity
) {
}
