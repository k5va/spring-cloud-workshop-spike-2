package by.javaguru.orderservice.client;

import by.javaguru.orderservice.client.dto.ProductDto;
import by.javaguru.orderservice.exception.ProductNotFoundException;
import by.javaguru.orderservice.exception.ProductServiceUnavailableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class RestClientProductClient implements ProductClient {

    private final RestClient restClient;

    public RestClientProductClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ProductDto getProduct(Long productId) {
        try {
            return restClient.get()
                    .uri("/products/{id}", productId)
                    .retrieve()
                    .body(ProductDto.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException(productId);
        } catch (RestClientResponseException | ResourceAccessException ex) {
            throw new ProductServiceUnavailableException(ex);
        }
    }
}
