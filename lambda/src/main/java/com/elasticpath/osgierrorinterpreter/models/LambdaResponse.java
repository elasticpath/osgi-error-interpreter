/*
 * Copyright (c) Elastic Path Software Inc., 2020
 */
package com.elasticpath.osgierrorinterpreter.models;

/**
 * Represents an API Gateway Response from AWS Lambda.
 */
public class LambdaResponse {

	private String errorInterpretation;
	private String solution;
	private String epSolution;

	public LambdaResponse(String errorInterpretation, String solution, String epSolution) {
		this.errorInterpretation = errorInterpretation;
		this.solution = solution;
		this.epSolution = epSolution;
	}

	public String getErrorInterpretation() {
		return errorInterpretation;
	}

	public String getSolution() {
		return solution;
	}

	public String getEpSolution() {
		return epSolution;
	}
}