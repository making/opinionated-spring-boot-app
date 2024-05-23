package com.example.opinionated.todo;

import java.util.UUID;

import am.ik.yavi.arguments.Arguments3Validator;
import am.ik.yavi.validator.Yavi;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

public record Todo(UUID id, String title, boolean completed) {

	private static final Arguments3Validator<UUID, String, Boolean, Todo> validator = Yavi.arguments()
		.<UUID>_object("id", c -> c.notNull())
		._string("title", c -> c.notBlank().greaterThanOrEqual(2).lessThanOrEqual(255))
		._boolean("completed", c -> c.notNull())
		.apply(Todo::new);

	@Builder(style = BuilderStyle.STAGED, toBuilder = "from")
	@JsonCreator
	public static Todo validated(UUID id, String title, Boolean completed) {
		return validator.validated(id, title, completed);
	}

}
