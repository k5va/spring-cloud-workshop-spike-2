package by.javaguru.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductServiceClientConfig {

    // Plain builder kept @Primary so unqualified injection points (e.g. the Eureka
    // client's own HTTP calls to discovery-server) don't pick up the load-balanced one below.
    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
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
