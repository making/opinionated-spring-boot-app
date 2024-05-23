package com.example.opinionated.message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

	private final List<Message> messages = new CopyOnWriteArrayList<>();

	@GetMapping(path = "/api/messages")
	public List<Message> getMessages() {
		return this.messages;
	}

	@PostMapping(path = "/api/messages")
	public Message postMessages(@RequestBody Message message) {
		this.messages.add(message);
		return message;
	}

}
