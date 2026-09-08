package by.javaguru.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductServiceClientConfig {

    // Declaring a RestClient.Builder bean here suppresses Boot's own default one
    // (RestClientAutoConfiguration backs off once any such bean exists), so without this
    // @Primary bean the Eureka client's internal registration/heartbeat calls would pick up
    // the @LoadBalanced builder below and fail trying to load-balance "discovery-server".
    @Bean
    @Primary
    public RestClient.Builder defaultRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient productServiceRestClient(@LoadBalanced RestClient.Builder restClientBuilder) {
        return restClientBuilder.baseUrl("http://product-service").build();
    }
}
