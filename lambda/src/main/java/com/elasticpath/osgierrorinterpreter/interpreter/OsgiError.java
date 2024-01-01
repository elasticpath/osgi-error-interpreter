package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.Optional;

import org.thymeleaf.TemplateEngine;

/**
 * An interface for classes that parse error strings.
 */
public interface OsgiError {
	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	static Optional<OsgiError> parse(String string) {
		MissingRequirementOsgiError missingRequirementOsgiError = MissingRequirementOsgiError.parse(string);
		if (missingRequirementOsgiError != null) {
			return Optional.of(missingRequirementOsgiError);
		}
		TimedOutOsgiError timedOutOsgiError = TimedOutOsgiError.parse(string);
		if (timedOutOsgiError != null) {
			return Optional.of(timedOutOsgiError);
		}
		SaxParseError saxParseError = SaxParseError.parse(string);
		if (saxParseError != null) {
			return Optional.of(saxParseError);
		}
		NotUniqueOsgiError notUniqueOsgiError = NotUniqueOsgiError.parse(string);
		if (notUniqueOsgiError != null) {
			return Optional.of(notUniqueOsgiError);
		}
		return Optional.empty();
	}

	/**
	 * Get the generated HTML for the error interpretation.
	 * @return the generated HTML for the error interpretation
	 */
	String getErrorInterpretation();

	/**
	 * Get the generated PlantUML for the error interpretation.
	 * @return the generated PlantUML for the error interpretation
	 */
	String getErrorInterpretationDiagram();

	/**
	 * Get the generated HTML for the generic solution.
	 * @param templateEngine the template engine
	 * @return the generated HTML for the generic solution
	 */
	String getSolutionHtml(TemplateEngine templateEngine);

	/**
	 * Get the generated HTML for the Elastic Path Self-Commerce solution.
	 * @param templateEngine the template engine
	 * @return the generated HTML for the Elastic Path Self-Commerce solution
	 */
	String getEPSolutionHtml(TemplateEngine templateEngine);
}
