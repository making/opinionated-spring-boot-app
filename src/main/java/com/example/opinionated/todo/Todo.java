package com.example.opinionated.todo;

import java.util.UUID;

import am.ik.yavi.arguments.Arguments3Validator;
import am.ik.yavi.validator.Yavi;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

@Builder(style = BuilderStyle.STAGED, toBuilder = "from")
public record Todo(UUID id, String title, Boolean completed) {

	private static final Arguments3Validator<UUID, String, Boolean, Todo> validator = Yavi.arguments()
		.<UUID>_object("id", c -> c.notNull())
		._string("title", c -> c.notBlank().greaterThanOrEqual(2).lessThanOrEqual(255))
		._boolean("completed", c -> c.notNull())
		.apply(Todo::new);

	public Todo {
		validator.lazy().validated(id, title, completed);
	}
}
