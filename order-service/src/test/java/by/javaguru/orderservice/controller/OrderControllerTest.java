package by.javaguru.orderservice.controller;

import by.javaguru.orderservice.client.ProductClient;
import by.javaguru.orderservice.client.dto.ProductDto;
import by.javaguru.orderservice.exception.ProductNotFoundException;
import by.javaguru.orderservice.exception.ProductServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductClient productClient;

    @Test
    void createOrder_validRequest_returns201WithLocationAndComputedPrice() throws Exception {
        when(productClient.getProduct(eq(1L)))
                .thenReturn(new ProductDto(1L, "Laptop", new BigDecimal("1200.00")));

        mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content("{\"productId\":1,\"quantity\":3}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.unitPrice").value(1200.00))
                .andExpect(jsonPath("$.totalPrice").value(3600.00));
    }

    @Test
    void createOrder_unknownProduct_returns404() throws Exception {
        when(productClient.getProduct(eq(999L))).thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content("{\"productId\":999,\"quantity\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void createOrder_nonPositiveQuantity_returns400() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content("{\"productId\":1,\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createOrder_productServiceUnavailable_returns5xx() throws Exception {
        when(productClient.getProduct(eq(1L)))
                .thenThrow(new ProductServiceUnavailableException(new RuntimeException("connection refused")));

        mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content("{\"productId\":1,\"quantity\":1}"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("PRODUCT_SERVICE_UNAVAILABLE"));
    }

    @Test
    void getAllOrders_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderById_existingOrder_returnsOrder() throws Exception {
        when(productClient.getProduct(eq(2L)))
                .thenReturn(new ProductDto(2L, "Mouse", new BigDecimal("25.50")));

        String location = mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content("{\"productId\":2,\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(2))
                .andExpect(jsonPath("$.unitPrice").value(25.50))
                .andExpect(jsonPath("$.totalPrice").value(51.00));
    }

    @Test
    void getOrderById_missingOrder_returns404() throws Exception {
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
