package com.example.opinionated.problem;

import java.util.Optional;

import am.ik.yavi.core.ConstraintViolationsException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Order(-1)
public class ProblemControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(ProblemControllerAdvice.class);

	private final Tracer tracer;

	public ProblemControllerAdvice(Optional<Tracer> tracer) {
		this.tracer = tracer.orElseGet(() -> {
			log.warn("Tracer is not found. NOOP trace is used instead.");
			return Tracer.NOOP; /* for test */
		});
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ProblemDetail handleResponseStatusException(ResponseStatusException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatus(e.getStatusCode());
		problemDetail.setDetail(e.getReason());
		return setTraceId(problemDetail);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		Throwable cause = e.getCause();
		if (cause instanceof ValueInstantiationException && cause.getCause() instanceof ConstraintViolationsException) {
			return handleConstraintViolationsException((ConstraintViolationsException) cause.getCause());
		}
		return handleRuntimeException(e);
	}

	@ExceptionHandler(ConstraintViolationsException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleConstraintViolationsException(ConstraintViolationsException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"Constraint violations found!");
		problemDetail.setProperty("violations", e.violations().details());
		return setTraceId(problemDetail);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ProblemDetail handleNoResourceFoundException(NoResourceFoundException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
		return setTraceId(problemDetail);
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ProblemDetail handleRuntimeException(RuntimeException e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"Unexpected runtime error occurred!");
		log.error("Unexpected runtime error occurred!", e);
		return setTraceId(problemDetail);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ProblemDetail handleException(Exception e) {
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"Unexpected error occurred!");
		log.error("Unexpected error occurred!", e);
		return setTraceId(problemDetail);
	}

	private ProblemDetail setTraceId(ProblemDetail problemDetail) {
		final Span currentSpan = tracer.currentSpan();
		if (currentSpan != null) {
			problemDetail.setProperty("traceId", currentSpan.context().traceId());
		}
		return problemDetail;
	}

}
