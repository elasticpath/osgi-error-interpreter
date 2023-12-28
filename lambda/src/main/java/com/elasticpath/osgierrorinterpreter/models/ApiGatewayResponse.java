package com.elasticpath.osgierrorinterpreter.models;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Defines the JSON representation of an API Gateway Response for AWS Lambdas.
 * See https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-output-format
 */
public class ApiGatewayResponse {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final int statusCode;
	private final String body;
	private final Map<String, Object> headers;
	private final boolean isBase64Encoded;

	/**
	 * Constructor for ApiGatewayResponse.
	 *
	 * @param statusCode      the response status code
	 * @param body            the response body
	 * @param headers         the response headers
	 * @param isBase64Encoded the isBase64Encoded flag
	 */
	public ApiGatewayResponse(final int statusCode, final String body, final Map<String, Object> headers, final boolean isBase64Encoded) {
		this.statusCode = statusCode;
		this.body = body;
		this.headers = headers;
		this.isBase64Encoded = isBase64Encoded;
	}

	/**
	 * Get the HTTP status code.
	 * @return the HTTP status code
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Get the body of the response.
	 * @return the body of the response.
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Get the headers in the response.
	 * @return the headers in the response
	 */
	public Map<String, Object> getHeaders() {
		return headers;
	}

	/**
	 * Is the body of the response base64 encoded?
	 * @return true if the body of the response is base64 encoded
	 */
	public boolean isIsBase64Encoded() {
		return isBase64Encoded;
	}

	/**
	 * Return new instance of Builder for {@link ApiGatewayResponse}.
	 *
	 * @return new instance of Builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder for {@link ApiGatewayResponse}.
	 */
	public static class Builder {
		private int statusCode;
		private final Map<String, Object> headers = new HashMap<>();
		private String rawBody;
		private Object objectBody;
		private byte[] binaryBody;
		private boolean base64Encoded;

		/**
		 * Set status code field of this builder.
		 *
		 * @param statusCode the status code
		 * @return this builder
		 */
		public Builder setStatusCode(final int statusCode) {
			this.statusCode = statusCode;
			return this;
		}

		/**
		 * Set headers field of this builder.
		 *
		 * @param key the header key
		 * @param value the header value
		 * @return this builder
		 */
		public Builder addHeader(final String key, final Object value) {
			this.headers.put(key, value);
			return this;
		}

		/**
		 * Builds the {@link ApiGatewayResponse} using the passed raw body string.
		 *
		 * @param rawBody the raw body string
		 * @return this builder
		 */
		public Builder setRawBody(final String rawBody) {
			this.rawBody = rawBody;
			return this;
		}

		/**
		 * Builds the {@link ApiGatewayResponse} using the passed object body
		 * converted to JSON.
		 *
		 * @param objectBody the object body
		 * @return this builder
		 */
		public Builder setObjectBody(final Object objectBody) {
			this.objectBody = objectBody;
			return this;
		}

		/**
		 * Builds the {@link ApiGatewayResponse} using the passed binary body
		 * encoded as base64. {@link #setBase64Encoded(boolean)
		 * setBase64Encoded(true)} will be in invoked automatically.
		 *
		 * @param binaryBody the binary body
		 * @return this builder
		 */
		public Builder setBinaryBody(final byte[] binaryBody) {

			if (binaryBody == null) {
				this.binaryBody = new byte[0];
			} else {
				this.binaryBody = Arrays.copyOf(binaryBody, binaryBody.length);
			}

			setBase64Encoded(true);
			return this;
		}

		/**
		 * A binary or rather a base64encoded responses requires.
		 * <ol>
		 * <li>"Binary Media Types" to be configured in API Gateway
		 * <li>a request with an "Accept" header set to one of the "Binary Media
		 * Types"
		 * </ol>
		 *
		 * @param base64Encoded the base64Encoded flag
		 * @return this builder
		 */
		public Builder setBase64Encoded(final boolean base64Encoded) {

			this.base64Encoded = base64Encoded;
			return this;
		}

		/**
		 * Return new instance of {@link ApiGatewayResponse}.
		 *
		 * @return new instance of ApiGatewayResponse
		 */
		public ApiGatewayResponse build() {

			if (rawBody != null) {
				return new ApiGatewayResponse(
						this.statusCode,
						rawBody,
						this.headers,
						this.base64Encoded);
			}

			if (objectBody != null) {
				try {
					return new ApiGatewayResponse(
							this.statusCode,
							OBJECT_MAPPER.writeValueAsString(objectBody),
							this.headers,
							this.base64Encoded);
				} catch (JsonProcessingException ex) {
					throw new RuntimeException("Failed to serialize object", ex);
				}
			}

			if (binaryBody != null) {
				return new ApiGatewayResponse(
						this.statusCode,
						new String(Base64.getEncoder().encode(binaryBody), StandardCharsets.UTF_8),
						this.headers,
						this.base64Encoded);
			}

			return new ApiGatewayResponse(
					this.statusCode,
					null,
					this.headers,
					this.base64Encoded);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("statusCode", statusCode)
				.append("isBase64Encoded", this.isBase64Encoded)
				.append("headers", this.headers)
				.append("body", this.body)
				.toString();
	}
}
