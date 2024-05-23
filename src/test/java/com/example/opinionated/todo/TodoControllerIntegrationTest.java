package com.example.opinionated.todo;

import java.net.URI;

import com.example.opinionated.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class TodoControllerIntegrationTest extends IntegrationTestBase {

	@Test
	void createGetUpdateGetDeleteGet() {
		String id;
		{
			ResponseEntity<String> response = this.restClient.post()
				.uri("/api/todos")
				.contentType(MediaType.APPLICATION_JSON)
				.body("""
						{"title": "Write a test"}
						""")
				.retrieve()
				.toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			URI location = response.getHeaders().getLocation();
			assertThat(location).isNotNull();
			assertThat(location.toString()).startsWith("http://localhost:" + this.port + "/api/todos/");
			id = location.toString().replace("http://localhost:" + this.port + "/api/todos/", "");
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathStringValue("$.id").isEqualTo(id);
			assertThat(content).extractingJsonPathStringValue("$.title").isEqualTo("Write a test");
			assertThat(content).extractingJsonPathBooleanValue("$.completed").isFalse();
		}
		{
			ResponseEntity<String> response = this.restClient.get()
				.uri("/api/todos/{id}", id)
				.retrieve()
				.toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathStringValue("$.id").isEqualTo(id);
			assertThat(content).extractingJsonPathStringValue("$.title").isEqualTo("Write a test");
			assertThat(content).extractingJsonPathBooleanValue("$.completed").isFalse();
		}
		{
			ResponseEntity<String> response = this.restClient.get().uri("/api/todos").retrieve().toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathNumberValue("$.length()").isEqualTo(1);
			assertThat(content).extractingJsonPathStringValue("$[0].id").isEqualTo(id);
			assertThat(content).extractingJsonPathStringValue("$[0].title").isEqualTo("Write a test");
			assertThat(content).extractingJsonPathBooleanValue("$[0].completed").isFalse();
		}
		{
			ResponseEntity<String> response = this.restClient.post().uri("/api/todos").body("""
					{}
					""").contentType(MediaType.APPLICATION_JSON).retrieve().toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathStringValue("$.detail").isEqualTo("Constraint violations found!");
			assertThat(content).extractingJsonPathNumberValue("$.violations.length()").isEqualTo(1);
			assertThat(content).extractingJsonPathStringValue("$.violations[0].defaultMessage")
				.isEqualToIgnoringNewLines("""
						"title" must not be blank
						""");
		}
		{
			ResponseEntity<String> response = this.restClient.put()
				.uri("/api/todos/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.body("""
						{"title": "a", "completed": true}
						""")
				.retrieve()
				.toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathStringValue("$.detail").isEqualTo("Constraint violations found!");
			assertThat(content).extractingJsonPathNumberValue("$.violations.length()").isEqualTo(1);
			assertThat(content).extractingJsonPathStringValue("$.violations[0].defaultMessage")
				.isEqualToIgnoringNewLines("""
						The size of "title" must be greater than or equal to 2. The given size is 1
						""");
		}
		{
			ResponseEntity<String> response = this.restClient.put()
				.uri("/api/todos/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.body("""
						{"title": "write a test!", "completed": true}
						""")
				.retrieve()
				.toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathStringValue("$.id").isEqualTo(id);
			assertThat(content).extractingJsonPathStringValue("$.title").isEqualTo("write a test!");
			assertThat(content).extractingJsonPathBooleanValue("$.completed").isTrue();
		}
		{
			ResponseEntity<String> response = this.restClient.get().uri("/api/todos").retrieve().toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathNumberValue("$.length()").isEqualTo(1);
			assertThat(content).extractingJsonPathStringValue("$[0].id").isEqualTo(id);
			assertThat(content).extractingJsonPathStringValue("$[0].title").isEqualTo("write a test!");
			assertThat(content).extractingJsonPathBooleanValue("$[0].completed").isTrue();
		}
		{
			ResponseEntity<String> response = this.restClient.delete()
				.uri("/api/todos/{id}", id)
				.retrieve()
				.toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		}
		{
			ResponseEntity<String> response = this.restClient.get().uri("/api/todos").retrieve().toEntity(String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			JsonContent<Object> content = this.json.from(response.getBody());
			assertThat(content).extractingJsonPathNumberValue("$.length()").isEqualTo(0);
		}
	}

}