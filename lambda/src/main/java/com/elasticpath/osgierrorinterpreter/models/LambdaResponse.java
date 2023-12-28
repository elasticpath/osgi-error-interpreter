package com.elasticpath.osgierrorinterpreter.models;

/**
 * Defines the JSON representation of the response from the Lambda.
 */
public class LambdaResponse {

	private final String errorInterpretation;
	private final String solution;
	private final String epSolution;

	/**
	 * Constructor.
	 * @param errorInterpretation the error interpretation
	 * @param solution the generic solution
	 * @param epSolution the Elastic Path Self-Managed solution
	 */
	public LambdaResponse(final String errorInterpretation, final String solution, final String epSolution) {
		this.errorInterpretation = errorInterpretation;
		this.solution = solution;
		this.epSolution = epSolution;
	}

	/**
	 * Get the error interpretation.
	 * @return the error interpretation
	 */
	public String getErrorInterpretation() {
		return errorInterpretation;
	}

	/**
	 * Get the generic solution.
	 * @return the generic solution
	 */
	public String getSolution() {
		return solution;
	}

	/**
	 * Get the Elastic Path Self-Managed solution.
	 * @return the Elastic Path Self-Managed solution
	 */
	public String getEpSolution() {
		return epSolution;
	}
}
