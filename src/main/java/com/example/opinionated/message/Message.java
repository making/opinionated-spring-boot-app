package com.example.opinionated.message;

import jakarta.annotation.Nullable;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

@Builder(style = BuilderStyle.STAGED)
public record Message(@Nullable String text) {
}
