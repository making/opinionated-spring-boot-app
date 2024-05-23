package com.example.opinionated.message;

import am.ik.yavi.core.ValueValidator;
import am.ik.yavi.validator.Yavi;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.annotation.Nullable;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

public record Message(String text) {

	private static final ValueValidator<String, Message> validator = Yavi.arguments()
		._string("text", c -> c.notBlank().greaterThanOrEqual(2).lessThanOrEqual(255))
		.apply(Message::new);

	@Builder(style = BuilderStyle.STAGED)
	@JsonCreator
	public static Message validated(@Nullable String text) {
		return validator.validated(text);
	}
}
