package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.Optional;

import org.thymeleaf.TemplateEngine;

public interface OsgiError {
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

	String getSolutionHtml(TemplateEngine templateEngine);

	String getEPSolutionHtml(TemplateEngine templateEngine);
}
