package com.example.opinionated.todo;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import static com.example.opinionated.todo.TodoBuilder.todo;

@RestController
public class TodoController {

	private final Map<UUID, Todo> todos = Collections.synchronizedMap(new LinkedHashMap<>());

	@GetMapping(path = "/api/todos")
	public Collection<Todo> getTodos() {
		return this.todos.values();
	}

	@PostMapping(path = "/api/todos")
	public ResponseEntity<Todo> postTodos(@RequestBody CreateTodoRequest request,
			UriComponentsBuilder uriComponentsBuilder) {
		Todo todo = todo().id(UUID.randomUUID()).title(request.title()).completed(false).build();
		this.todos.put(todo.id(), todo);
		return ResponseEntity.created(uriComponentsBuilder.path("/api/todos/{id}").build(todo.id())).body(todo);
	}

	@GetMapping(path = "/api/todos/{id}")
	public Todo getTodo(@PathVariable UUID id) {
		Todo todo = this.todos.get(id);
		if (todo == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The request todo is not found.");
		}
		return todo;
	}

	@PutMapping(path = "/api/todos/{id}")
	public Todo putTodos(@PathVariable UUID id, @RequestBody UpdateTodoRequest request) {
		Todo todo = this.getTodo(id);
		TodoBuilder builder = TodoBuilder.from(todo);
		if (request.title() != null) {
			builder.title(request.title());
		}
		if (request.completed() != null) {
			builder.completed(request.completed());
		}
		Todo updated = builder.build();
		this.todos.put(id, updated);
		return updated;
	}

	@DeleteMapping(path = "/api/todos/{id}")
	public ResponseEntity<Void> deleteTodo(@PathVariable UUID id) {
		this.todos.remove(id);
		return ResponseEntity.noContent().build();
	}

	public record CreateTodoRequest(String title) {

	}

	public record UpdateTodoRequest(String title, Boolean completed) {

	}

}
