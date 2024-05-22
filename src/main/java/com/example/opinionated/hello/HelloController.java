package com.example.opinionated.hello;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nullable;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class HelloController {

	private final RestClient restClient;

	public HelloController(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.baseUrl("https://httpbin.org").build();
	}

	@GetMapping(path = "/api/hello")
	public JsonNode getHello(@RequestParam(defaultValue = "Hello World!") String message) {
		return this.restClient.get().uri("/get?message={message}", message).retrieve().body(JsonNode.class).get("args");
	}

	@PostMapping(path = "/api/hello")
	public JsonNode postHello(@RequestBody Hello hello) {
		return this.restClient.post()
			.uri("/post")
			.contentType(MediaType.APPLICATION_JSON)
			.body(hello)
			.retrieve()
			.body(JsonNode.class)
			.get("json");
	}

	@Builder(style = BuilderStyle.STAGED)
	public record Hello(@Nullable String message) {

	}

}
