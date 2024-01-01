package com.elasticpath.osgierrorinterpreter.models;

/**
 * Defines the JSON representation of the response from the Lambda.
 */
public class LambdaResponse {

	private final String errorInterpretation;
	private final String errorInterpretationDiagram;
	private final String solution;
	private final String epSolution;

	/**
	 * Constructor.
	 * @param errorInterpretation the error interpretation
	 * @param errorInterpretationDiagram the error interpretation diagram
	 * @param solution the generic solution
	 * @param epSolution the Elastic Path Self-Managed solution
	 */
	public LambdaResponse(final String errorInterpretation, final String errorInterpretationDiagram, final String solution, final String epSolution) {
		this.errorInterpretation = errorInterpretation;
		this.errorInterpretationDiagram = errorInterpretationDiagram;
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
	 * Get the error interpretation diagram.
	 * @return the error interpretation diagram
	 */
	public String getErrorInterpretationDiagram() {
		return errorInterpretationDiagram;
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
