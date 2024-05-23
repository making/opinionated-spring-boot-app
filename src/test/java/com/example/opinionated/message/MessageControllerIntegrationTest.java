package com.example.opinionated.message;

import com.example.opinionated.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class MessageControllerIntegrationTest extends IntegrationTestBase {

	@Test
	void postAndGetMessages() {
		{
			ResponseEntity<String> response = this.restClient.post().uri("/api/messages").body("""
					{"text":  "Hello World 1!"}
					""").contentType(MediaType.APPLICATION_JSON).retrieve().toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("Hello World 1!");
		}
		{
			ResponseEntity<String> response = this.restClient.post().uri("/api/messages").body("""
					{"text":  "Hello World 2!"}
					""").contentType(MediaType.APPLICATION_JSON).retrieve().toEntity(String.class);
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

	@Test
	void postInvalidMessage() {
		ResponseEntity<String> response = this.restClient.post().uri("/api/messages").body("""
				{"text":  "a"}
				""").contentType(MediaType.APPLICATION_JSON).retrieve().toEntity(String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		JsonContent<Object> content = this.json.from(response.getBody());
		assertThat(content).extractingJsonPathStringValue("$.detail").isEqualTo("Constraint violations found!");
		assertThat(content).extractingJsonPathNumberValue("$.violations.length()").isEqualTo(1);
		assertThat(content).extractingJsonPathStringValue("$.violations[0].defaultMessage")
			.isEqualToIgnoringNewLines("""
					The size of "text" must be greater than or equal to 2. The given size is 1
					""");
	}

}