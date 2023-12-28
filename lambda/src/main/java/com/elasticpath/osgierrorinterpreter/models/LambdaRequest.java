package com.elasticpath.osgierrorinterpreter.models;

/**
 * Defines the JSON representation of the expected Lambda request.
 */
public class LambdaRequest {
	private String errorMessage;

	/**
	 * Gets the error message.
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets the error message.
	 * @param errorMessage the error message
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
