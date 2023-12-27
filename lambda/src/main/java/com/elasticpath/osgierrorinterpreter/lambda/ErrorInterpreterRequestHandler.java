package com.elasticpath.osgierrorinterpreter.lambda;

import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.elasticpath.osgierrorinterpreter.models.ApiGatewayResponse;
import com.elasticpath.osgierrorinterpreter.models.LambdaRequest;
import com.elasticpath.osgierrorinterpreter.models.LambdaResponse;
import com.elasticpath.osgierrorinterpreter.interpreter.OsgiError;

/**
 * Represents AWS Lambda function for OSGi error interpretation.
 */
public class ErrorInterpreterRequestHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private final TemplateEngine templateEngine;

	public ErrorInterpreterRequestHandler() {
		templateEngine = new TemplateEngine();
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode("HTML");
		templateResolver.setSuffix(".html");
		templateEngine.setTemplateResolver(templateResolver);
	}

	@Override
	public ApiGatewayResponse handleRequest(final Map<String, Object> requestMap, final Context context) {
		LambdaRequest lambdaRequest;
		try {
			lambdaRequest = OBJECT_MAPPER.readValue((String) requestMap.get("body"), LambdaRequest.class);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException("Unable to serialize body.", ex);
		}
		LambdaResponse response;
		Optional<OsgiError> osgiErrorOptional = OsgiError.parse(lambdaRequest.getErrorMessage());
		if (osgiErrorOptional.isPresent()) {
			OsgiError osgiError = osgiErrorOptional.get();
			response = new LambdaResponse(osgiError.toString(), osgiError.getSolutionHtml(templateEngine), osgiError.getEPSolutionHtml(templateEngine));
		} else {
			response = new LambdaResponse("Unknown", "Unknown", "Unknown");
		}
		return ApiGatewayResponse.builder()
				.addHeader("Access-Control-Allow-Headers", "Content-Type")
				.addHeader("Access-Control-Allow-Origin", "*")
				.addHeader("Access-Control-Allow-Methods", "POST")
				.setStatusCode(200)
				.setObjectBody(response)
				.build();
	}
}
