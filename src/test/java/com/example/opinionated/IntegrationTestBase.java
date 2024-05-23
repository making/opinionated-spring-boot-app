package com.example.opinionated;

import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

	@Autowired
	protected RestClient.Builder restClientBuilder;

	protected RestClient restClient;

	@LocalServerPort
	protected int port;

	protected BasicJsonTester json = new BasicJsonTester(getClass());

	@BeforeEach
	public void init() {
		if (this.restClient == null) {
			this.restClient = this.restClientBuilder.baseUrl("http://localhost:" + port)
				.defaultStatusHandler(new DefaultResponseErrorHandler() {
					@Override
					protected void handleError(ClientHttpResponse response, HttpStatusCode statusCode) {

					}
				})
				.build();
		}
	}

}
