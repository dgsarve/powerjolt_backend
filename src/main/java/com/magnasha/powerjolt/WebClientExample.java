package com.magnasha.powerjolt;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import com.magnasha.powerjolt.wsdto.TransformRequest;

import reactor.core.publisher.Mono;


public class WebClientExample
{

	public static void main(String[] args)
	{
		callTrasnform();

	}

	public static void callTrasnform()
	{

		WebClient webClient = WebClient.create("http://localhost:8080");

		TransformRequest request = new TransformRequest();
		request.setInputJson("{\n" + "  \"rating\": {\n" + "    \"primary\": {\n" + "      \"value\": 3\n" + "    },\n"
				+ "    \"quality\": {\n" + "      \"value\": 3\n" + "    }\n" + "  }\n" + "}\n");
		request.setSpecJson("[{\"operation\":\"shift\",\"spec\":{\"rating\":{\"primary\":{\"value\":\"Rating\",\"max\":\"RatingRange\"},\"*\":{\"max\":\"SecondaryRatings.&1.Range\",\"value\":\"SecondaryRatings.&1.Value\",\"$\":\"SecondaryRatings.&1.Id\"}}}},{\"operation\":\"default\",\"spec\":{\"Range\":5,\"SecondaryRatings\":{\"*\":{\"Range\":5}}}}]");


		Mono<Map> response = webClient.post().uri("/api/transform").bodyValue(request)
				.header(HttpHeaders.AUTHORIZATION, basicAuth("user", "password")).header("x-api-key", "testapikey").retrieve()
				.bodyToMono(Map.class);

		response.subscribe(res ->
		{
			System.out.println("Response: " + res);
		});


		response.subscribe(responseBody -> System.out.println("Response Body: " + responseBody),
				error -> System.err.println("Error: " + error.getMessage()),
				() -> System.out.println("Request completed successfully" ));


		response.block();
	}

	public static void callGenerateSpec()
	{

		WebClient webClient = WebClient.create("http://localhost:8080");

		Map<String, String> requestBody = Map.of("input", "{\"inputKey\": \"value\"}", "output", "{\"outputKey\": \"value\"}");

		Mono<Map> response = webClient.post().uri("/api/generate-spec").bodyValue(requestBody)
				.header(HttpHeaders.AUTHORIZATION, basicAuth("user", "password")).header("x-api-key", "testapikey").retrieve()
				.bodyToMono(Map.class);

		response.subscribe(res ->
		{
			System.out.println("Response: " + res);
		});

		// Subscribe to the response and handle it asynchronously
		response.subscribe(responseBody -> System.out.println("Response Body: " + responseBody),
				error -> System.err.println("Error: " + error.getMessage()),
				() -> System.out.println("Request completed successfully" + requestBody));

		// Block until the request completes (for demonstration purposes only, not recommended in production)
		response.block();
	}

	private static String basicAuth(String username, String password)
	{
		String auth = username + ":" + password;
		byte[] encodedAuth = java.util.Base64.getEncoder().encode(auth.getBytes());
		return "Basic " + new String(encodedAuth);
	}
}
