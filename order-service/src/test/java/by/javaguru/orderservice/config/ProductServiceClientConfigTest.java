package by.javaguru.orderservice.config;

import by.javaguru.orderservice.client.ProductClient;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "eureka.client.enabled=false")
class ProductServiceClientConfigTest {

    private static HttpServer productServiceStub;

    @Autowired
    private ProductClient productClient;

    @BeforeAll
    static void startProductServiceStub() throws IOException {
        productServiceStub = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        productServiceStub.createContext("/products/1", exchange -> {
            byte[] body = "{\"id\":1,\"name\":\"Laptop\",\"price\":1200.00}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        productServiceStub.start();
    }

    @AfterAll
    static void stopProductServiceStub() {
        productServiceStub.stop(0);
    }

    @DynamicPropertySource
    static void discoveryProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.discovery.client.simple.instances.product-service[0].uri",
                () -> "http://localhost:" + productServiceStub.getAddress().getPort());
    }

    @Test
    void productServiceRestClient_resolvesLogicalServiceNameViaDiscovery_reachesRegisteredInstance() {
        var product = productClient.getProduct(1L);

        assertThat(product.id()).isEqualTo(1L);
        assertThat(product.name()).isEqualTo("Laptop");
    }
}
