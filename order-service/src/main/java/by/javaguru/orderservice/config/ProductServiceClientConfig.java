package by.javaguru.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductServiceClientConfig {

    @Bean
    public RestClient productServiceRestClient(RestClient.Builder restClientBuilder,
                                                @Value("${product-service.base-url}") String baseUrl) {
        return restClientBuilder.baseUrl(baseUrl).build();
    }
}
