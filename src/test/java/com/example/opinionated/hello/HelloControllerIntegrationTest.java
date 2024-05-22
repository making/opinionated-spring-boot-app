package com.example.opinionated.hello;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;

import static com.example.opinionated.hello.HelloBuilder.hello;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloControllerIntegrationTest {

	@Autowired
	RestClient.Builder restClientBuilder;

	RestClient restClient;

	@LocalServerPort
	int port;

	BasicJsonTester json = new BasicJsonTester(getClass());

	@BeforeEach
	public void init() {
		if (this.restClient == null) {
			this.restClient = this.restClientBuilder.baseUrl("http://localhost:" + port)
				.defaultStatusHandler(new DefaultResponseErrorHandler() {
					@Override
					protected void handleError(ClientHttpResponse response, HttpStatusCode statusCode) {

					}
				})
				.build();
		}
	}

	@Test
	void getHello() {
		ResponseEntity<String> response = this.restClient.get().uri("/api/hello").retrieve().toEntity(String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonContent<Object> content = this.json.from(response.getBody());
		assertThat(content).extractingJsonPathStringValue("$.message").isEqualTo("Hello World!");
	}

	@Test
	void getHelloWithParam() {
		ResponseEntity<String> response = this.restClient.get()
			.uri("/api/hello?message={message}", "Test!")
			.retrieve()
			.toEntity(String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonContent<Object> content = this.json.from(response.getBody());
		assertThat(content).extractingJsonPathStringValue("$.message").isEqualTo("Test!");
	}

	@Test
	void postHello() {
		ResponseEntity<String> response = this.restClient.post()
			.uri("/api/hello")
			.contentType(MediaType.APPLICATION_JSON)
			.body(hello().message("Hi!").build())
			.retrieve()
			.toEntity(String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonContent<Object> content = this.json.from(response.getBody());
		assertThat(content).extractingJsonPathStringValue("$.message").isEqualTo("Hi!");
	}

}
