package com.example.opinionated.hello;

import com.example.opinionated.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class HelloControllerIntegrationTest extends IntegrationTestBase {

	@Test
	void hello() {
		ResponseEntity<String> response = this.restClient.get().uri("/api/hello").retrieve().toEntity(String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonContent<Object> content = this.json.from(response.getBody());
		assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("Hello World!");
	}

	@Test
	void helloWithParam() {
		ResponseEntity<String> response = this.restClient.get()
			.uri("/api/hello?message={message}", "Test!")
			.retrieve()
			.toEntity(String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		JsonContent<Object> content = this.json.from(response.getBody());
		assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("Test!");
	}

}
