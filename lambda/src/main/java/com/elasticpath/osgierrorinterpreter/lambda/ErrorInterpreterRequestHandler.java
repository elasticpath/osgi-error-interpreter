package com.elasticpath.osgierrorinterpreter.lambda;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.elasticpath.osgierrorinterpreter.interpreter.OsgiError;
import com.elasticpath.osgierrorinterpreter.models.ApiGatewayResponse;
import com.elasticpath.osgierrorinterpreter.models.LambdaRequest;
import com.elasticpath.osgierrorinterpreter.models.LambdaResponse;

/**
 * Represents AWS Lambda function for OSGi error interpretation.
 */
public class ErrorInterpreterRequestHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final String UNKNOWN = "Unknown";
	private final TemplateEngine templateEngine;

	/**
	 * Constructor.
	 */
	public ErrorInterpreterRequestHandler() {
		templateEngine = new TemplateEngine();
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode("HTML");
		templateResolver.setSuffix(".html");
		templateEngine.setTemplateResolver(templateResolver);
	}

	@Override
	public ApiGatewayResponse handleRequest(final Map<String, Object> requestMap, final Context context) {
		LambdaResponse response;
		try {
			LambdaRequest lambdaRequest = OBJECT_MAPPER.readValue((String) requestMap.get("body"), LambdaRequest.class);
			Optional<OsgiError> osgiErrorOptional = OsgiError.parse(lambdaRequest.getErrorMessage());
			if (osgiErrorOptional.isPresent()) {
				OsgiError osgiError = osgiErrorOptional.get();
				response = new LambdaResponse(osgiError.getErrorInterpretation(), osgiError.getErrorInterpretationDiagram(),
						osgiError.getSolutionHtml(templateEngine), osgiError.getEPSolutionHtml(templateEngine));
			} else {
				response = new LambdaResponse("Unknown: error will be logged for investigation.", null, UNKNOWN, UNKNOWN);
				context.getLogger().log("Unknown Error Message Requested: " + lambdaRequest.getErrorMessage());
			}
			return ApiGatewayResponse.builder()
					.addHeader("Access-Control-Allow-Headers", "Content-Type")
					.addHeader("Access-Control-Allow-Origin", "*")
					.addHeader("Access-Control-Allow-Methods", "POST")
					.setStatusCode(200)
					.setObjectBody(response)
					.build();
		} catch (Exception ex) {
			response = new LambdaResponse("Unexpected Error: " + ex.getMessage(), null, UNKNOWN, UNKNOWN);
			context.getLogger().log("Unexpected error: " + getStackTrace(ex));
			return ApiGatewayResponse.builder()
					.addHeader("Access-Control-Allow-Headers", "Content-Type")
					.addHeader("Access-Control-Allow-Origin", "*")
					.addHeader("Access-Control-Allow-Methods", "POST")
					.setStatusCode(500)
					.setObjectBody(response)
					.build();
		}
	}

	private static String getStackTrace(final Throwable throwable) {
		StringWriter stackTraceHolder = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stackTraceHolder));
		return stackTraceHolder.toString();
	}
}
