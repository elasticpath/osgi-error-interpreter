package com.elasticpath.osgierrorinterpreter.lambda;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.elasticpath.osgierrorinterpreter.interpreter.OsgiError;
import com.elasticpath.osgierrorinterpreter.mcp.HtmlToTextConverter;
import com.elasticpath.osgierrorinterpreter.models.ApiGatewayResponse;

/**
 * AWS Lambda function that exposes the OSGi error interpreter as a stateless Model Context Protocol (MCP) server.
 * It speaks MCP over the Streamable HTTP transport using single JSON responses (no server-initiated SSE streams).
 */
public class McpRequestHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final String JSON_RPC_VERSION = "2.0";
	private static final String LATEST_PROTOCOL_VERSION = "2025-06-18";
	private static final String SERVER_NAME = "osgi-error-interpreter";
	private static final String SERVER_VERSION = "1.0.0";
	private static final String TOOL_NAME = "interpret_osgi_error";

	private static final int PARSE_ERROR = -32700;
	private static final int METHOD_NOT_FOUND = -32601;
	private static final int INVALID_PARAMS = -32602;
	private static final int INTERNAL_ERROR = -32603;

	private static final int STATUS_OK = 200;
	private static final int STATUS_ACCEPTED = 202;

	private final TemplateEngine templateEngine;

	/**
	 * Constructor.
	 */
	public McpRequestHandler() {
		templateEngine = new TemplateEngine();
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode("HTML");
		templateResolver.setSuffix(".html");
		templateEngine.setTemplateResolver(templateResolver);
	}

	@Override
	public ApiGatewayResponse handleRequest(final Map<String, Object> requestMap, final Context context) {
		JsonNode request;
		try {
			request = OBJECT_MAPPER.readTree((String) requestMap.get("body"));
		} catch (Exception ex) {
			return jsonResponse(STATUS_OK, errorResponse(null, PARSE_ERROR, "Parse error: " + ex.getMessage()));
		}

		JsonNode idNode = request.get("id");
		String method = request.path("method").asText("");

		// Requests without an id are JSON-RPC notifications; acknowledge them with no response body.
		if (idNode == null) {
			return noContentResponse();
		}

		try {
			switch (method) {
				case "initialize":
					return jsonResponse(STATUS_OK, successResponse(idNode, buildInitializeResult(request)));
				case "tools/list":
					return jsonResponse(STATUS_OK, successResponse(idNode, buildToolsListResult()));
				case "tools/call":
					return jsonResponse(STATUS_OK, successResponse(idNode, buildToolCallResult(request, context)));
				default:
					return jsonResponse(STATUS_OK, errorResponse(idNode, METHOD_NOT_FOUND, "Method not found: " + method));
			}
		} catch (McpException ex) {
			return jsonResponse(STATUS_OK, errorResponse(idNode, ex.getCode(), ex.getMessage()));
		} catch (Exception ex) {
			context.getLogger().log("Unexpected MCP error: " + getStackTrace(ex));
			return jsonResponse(STATUS_OK, errorResponse(idNode, INTERNAL_ERROR, "Internal error: " + ex.getMessage()));
		}
	}

	private JsonNode buildInitializeResult(final JsonNode request) {
		String requestedVersion = request.path("params").path("protocolVersion").asText(LATEST_PROTOCOL_VERSION);
		ObjectNode result = OBJECT_MAPPER.createObjectNode();
		result.put("protocolVersion", requestedVersion);
		result.putObject("capabilities").putObject("tools");
		ObjectNode serverInfo = result.putObject("serverInfo");
		serverInfo.put("name", SERVER_NAME);
		serverInfo.put("version", SERVER_VERSION);
		return result;
	}

	private JsonNode buildToolsListResult() {
		ObjectNode result = OBJECT_MAPPER.createObjectNode();
		ArrayNode tools = result.putArray("tools");
		ObjectNode tool = tools.addObject();
		tool.put("name", TOOL_NAME);
		tool.put("description", "Interpret an OSGi framework error message (for example a missing requirement, "
				+ "unresolved bundle, or SAX parse error) and return an explanation plus remediation guidance, "
				+ "including advice specific to Elastic Path Self-Managed Commerce.");
		ObjectNode inputSchema = tool.putObject("inputSchema");
		inputSchema.put("type", "object");
		ObjectNode properties = inputSchema.putObject("properties");
		ObjectNode errorMessage = properties.putObject("errorMessage");
		errorMessage.put("type", "string");
		errorMessage.put("description", "The full OSGi error message or stack trace to interpret.");
		inputSchema.putArray("required").add("errorMessage");
		return result;
	}

	private JsonNode buildToolCallResult(final JsonNode request, final Context context) {
		JsonNode params = request.path("params");
		String name = params.path("name").asText("");
		if (!TOOL_NAME.equals(name)) {
			throw new McpException(INVALID_PARAMS, "Unknown tool: " + name);
		}
		String errorMessage = params.path("arguments").path("errorMessage").asText("");
		if (errorMessage.trim().isEmpty()) {
			throw new McpException(INVALID_PARAMS, "Missing required argument: errorMessage");
		}

		String text;
		Optional<OsgiError> osgiErrorOptional = OsgiError.parse(errorMessage);
		if (osgiErrorOptional.isPresent()) {
			text = formatInterpretation(osgiErrorOptional.get());
		} else {
			text = "This OSGi error was not recognized by the interpreter, so no specific guidance is available. "
					+ "It has been logged for investigation. Supported error types include missing requirements, "
					+ "unresolved bundles, non-unique wiring, timed-out components, no-conversion-found, and SAX parse errors.";
			context.getLogger().log("Unknown Error Message Requested (MCP): " + errorMessage);
		}

		ObjectNode result = OBJECT_MAPPER.createObjectNode();
		ObjectNode content = result.putArray("content").addObject();
		content.put("type", "text");
		content.put("text", text);
		result.put("isError", false);
		return result;
	}

	private String formatInterpretation(final OsgiError osgiError) {
		String interpretation = HtmlToTextConverter.convert(osgiError.getErrorInterpretation());
		String solution = HtmlToTextConverter.convert(osgiError.getSolutionHtml(templateEngine));
		String epSolution = HtmlToTextConverter.convert(osgiError.getEPSolutionHtml(templateEngine));
		return "# Interpretation\n\n" + interpretation
				+ "\n\n# Solution\n\n" + solution
				+ "\n\n# Elastic Path Self-Managed Commerce solution\n\n" + epSolution;
	}

	private ObjectNode successResponse(final JsonNode id, final JsonNode result) {
		ObjectNode response = newEnvelope(id);
		response.set("result", result);
		return response;
	}

	private ObjectNode errorResponse(final JsonNode id, final int code, final String message) {
		ObjectNode response = newEnvelope(id);
		ObjectNode error = response.putObject("error");
		error.put("code", code);
		error.put("message", message);
		return response;
	}

	private ObjectNode newEnvelope(final JsonNode id) {
		ObjectNode response = OBJECT_MAPPER.createObjectNode();
		response.put("jsonrpc", JSON_RPC_VERSION);
		if (id == null) {
			response.putNull("id");
		} else {
			response.set("id", id);
		}
		return response;
	}

	private ApiGatewayResponse jsonResponse(final int statusCode, final JsonNode body) {
		return corsBuilder()
				.addHeader("Content-Type", "application/json")
				.setStatusCode(statusCode)
				.setObjectBody(body)
				.build();
	}

	private ApiGatewayResponse noContentResponse() {
		return corsBuilder()
				.setStatusCode(STATUS_ACCEPTED)
				.build();
	}

	private ApiGatewayResponse.Builder corsBuilder() {
		return ApiGatewayResponse.builder()
				.addHeader("Access-Control-Allow-Headers", "Content-Type")
				.addHeader("Access-Control-Allow-Origin", "*")
				.addHeader("Access-Control-Allow-Methods", "POST");
	}

	private static String getStackTrace(final Throwable throwable) {
		StringWriter stackTraceHolder = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stackTraceHolder));
		return stackTraceHolder.toString();
	}

	/**
	 * Internal exception used to signal a JSON-RPC error with a specific code back to the dispatcher.
	 */
	private static final class McpException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private final int code;

		private McpException(final int code, final String message) {
			super(message);
			this.code = code;
		}

		private int getCode() {
			return code;
		}
	}
}
