package com.example.opinionated.config;

import java.time.Duration;
import java.util.function.Predicate;

import am.ik.accesslogger.AccessLogger;
import am.ik.accesslogger.AccessLoggerBuilder;
import am.ik.spring.http.client.RetryableClientHttpRequestInterceptor;
import io.micrometer.core.instrument.config.MeterFilter;

import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

	@Bean
	public RestClientCustomizer restClientCustomizer() {
		return builder -> builder
			.requestFactory(ClientHttpRequestFactories.get(JdkClientHttpRequestFactory::new,
					ClientHttpRequestFactorySettings.DEFAULTS.withReadTimeout(Duration.ofSeconds(3))))
			.requestInterceptor(new RetryableClientHttpRequestInterceptor(new ExponentialBackOff() {
				{
					setMaxElapsedTime(12_000);
				}
			}));
	}

	@Bean
	public AccessLogger accessLogger() {
		Predicate<HttpExchange> excludeActuator = httpExchange -> {
			String uri = httpExchange.getRequest().getUri().getPath();
			return uri != null && !(uri.equals("/readyz") || uri.equals("/livez") || uri.startsWith("/actuator")
					|| uri.startsWith("/cloudfoundryapplication"));
		};
		Predicate<HttpExchange> excludeRoute53 = httpExchange -> {
			String userAgent = CollectionUtils.firstElement(httpExchange.getRequest().getHeaders().get("user-agent"));
			return userAgent != null && !userAgent.startsWith("Amazon-Route53-Health-Check-Service");
		};
		return AccessLoggerBuilder.accessLogger()
			.filter(excludeRoute53.and(excludeActuator))
			.addKeyValues(true)
			.build();
	}

	@Bean
	public MeterFilter customMeterFilter() {
		return MeterFilter.deny(id -> {
			String uri = id.getTag("uri");
			return uri != null && (uri.startsWith("/readyz") || uri.startsWith("/livez") || uri.startsWith("/actuator")
					|| uri.startsWith("/cloudfoundryapplication"));
		});
	}

}
