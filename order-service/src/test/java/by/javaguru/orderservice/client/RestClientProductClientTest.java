package by.javaguru.orderservice.client;

import by.javaguru.orderservice.exception.ProductNotFoundException;
import by.javaguru.orderservice.exception.ProductServiceUnavailableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class RestClientProductClientTest {

    private static final String BASE_URL = "http://product-service.test";

    private org.springframework.test.web.client.MockRestServiceServer mockServer;
    private ProductClient productClient;
    private ServerSocket closedSocket;

    private void setUpAgainst(String baseUrl) {
        RestClient.Builder builder = RestClient.builder();
        mockServer = org.springframework.test.web.client.MockRestServiceServer.bindTo(builder).build();
        productClient = new RestClientProductClient(builder, baseUrl);
    }

    @AfterEach
    void closeSocket() throws IOException {
        if (closedSocket != null) {
            closedSocket.close();
        }
    }

    @Test
    void getProduct_notFoundResponse_throwsProductNotFoundException() {
        setUpAgainst(BASE_URL);
        mockServer.expect(requestTo(BASE_URL + "/products/999"))
                .andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND));

        assertThatThrownBy(() -> productClient.getProduct(999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void getProduct_connectionRefused_throwsProductServiceUnavailableException() throws IOException {
        closedSocket = new ServerSocket(0);
        int unusedPort = closedSocket.getLocalPort();
        closedSocket.close();
        closedSocket = null;

        productClient = new RestClientProductClient(RestClient.builder(), "http://localhost:" + unusedPort);

        assertThatThrownBy(() -> productClient.getProduct(1L))
                .isInstanceOf(ProductServiceUnavailableException.class);
    }

    @Test
    void getProduct_success_returnsProduct() {
        setUpAgainst(BASE_URL);
        mockServer.expect(requestTo(BASE_URL + "/products/1"))
                .andExpect(method(GET))
                .andRespond(withSuccessJson("""
                        {"id":1,"name":"Laptop","price":1200.00}
                        """));

        var product = productClient.getProduct(1L);

        assertThat(product.id()).isEqualTo(1L);
        assertThat(product.name()).isEqualTo("Laptop");
    }

    private static org.springframework.test.web.client.ResponseCreator withSuccessJson(String body) {
        return org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess(
                body, org.springframework.http.MediaType.APPLICATION_JSON);
    }
}
