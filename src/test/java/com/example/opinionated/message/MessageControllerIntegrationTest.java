package com.example.opinionated.message;

import com.example.opinionated.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.example.opinionated.message.MessageBuilder.message;
import static org.assertj.core.api.Assertions.assertThat;

class MessageControllerIntegrationTest extends IntegrationTestBase {

	@Test
	void postAndGetMessages() {
		{
			ResponseEntity<String> response = this.restClient.post()
				.uri("/api/messages")
				.body(message().text("Hello World 1!").build())
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("Hello World 1!");
		}
		{
			ResponseEntity<String> response = this.restClient.post()
				.uri("/api/messages")
				.body(message().text("Hello World 2!").build())
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("Hello World 2!");
		}
		{
			ResponseEntity<String> response = this.restClient.get()
				.uri("/api/messages")
				.retrieve()
				.toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathNumberValue("$.length()").isEqualTo(2);
			assertThat(content).extractingJsonPathStringValue("$[0].text").isEqualTo("Hello World 1!");
			assertThat(content).extractingJsonPathStringValue("$[1].text").isEqualTo("Hello World 2!");
		}
	}

}