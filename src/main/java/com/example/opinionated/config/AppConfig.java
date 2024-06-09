package com.example.opinionated.config;

import java.time.Duration;

import am.ik.spring.http.client.RetryableClientHttpRequestInterceptor;
import io.micrometer.core.instrument.config.MeterFilter;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

	@Bean
	public RestClientCustomizer restClientCustomizer(
			LogbookClientHttpRequestInterceptor logbookClientHttpRequestInterceptor) {
		return builder -> builder
			.requestFactory(ClientHttpRequestFactories.get(JdkClientHttpRequestFactory::new,
					ClientHttpRequestFactorySettings.DEFAULTS.withReadTimeout(Duration.ofSeconds(3))))
			.requestInterceptor(logbookClientHttpRequestInterceptor)
			.requestInterceptor(new RetryableClientHttpRequestInterceptor(new ExponentialBackOff() {
				{
					setMaxElapsedTime(12_000);
				}
			}));
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
