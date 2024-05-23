package com.example.opinionated.hello;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
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
	public JsonNode hello(@RequestParam(defaultValue = "Hello World!") String message) {
		return this.restClient.get().uri("/get?text={message}", message).retrieve().body(JsonNode.class).get("args");
	}

}
