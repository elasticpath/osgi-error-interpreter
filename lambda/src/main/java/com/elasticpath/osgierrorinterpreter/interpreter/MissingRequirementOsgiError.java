package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Parses errors regarding missing package requirements.
 */
public class MissingRequirementOsgiError implements OsgiError {
	private static final Pattern WITH_CAUSED_BY_PATTERN = Pattern.compile("Unable to resolve ([^:]+): missing requirement (.+?) \\[caused by: (.+)\\]");
	private static final Pattern WITHOUT_CAUSED_BY_PATTERN = Pattern.compile("Unable to resolve ([^:]+): missing requirement (.+)");

	private final Resource resource;
	private final Requirement requirement;
	private final OsgiError causedBy;

	/**
	 * Constructor.
	 * @param resource the resource
	 * @param requirement the requirement
	 * @param causedBy the caused by
	 */
	public MissingRequirementOsgiError(final Resource resource, final Requirement requirement, final OsgiError causedBy) {
		this.resource = resource;
		this.requirement = requirement;
		this.causedBy = causedBy;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static MissingRequirementOsgiError parse(final String string) {
		Matcher withCausedByMatcher = WITH_CAUSED_BY_PATTERN.matcher(string);
		if (withCausedByMatcher.find()) {
			return new MissingRequirementOsgiError(Resource.parse(withCausedByMatcher.group(1)), Requirement.parse(withCausedByMatcher.group(2)),
					MissingRequirementOsgiError.parse(withCausedByMatcher.group(3)));
		}
		Matcher withoutCausedByMatcher = WITHOUT_CAUSED_BY_PATTERN.matcher(string);
		if (withoutCausedByMatcher.find()) {
			return new MissingRequirementOsgiError(Resource.parse(withoutCausedByMatcher.group(1)),
					Requirement.parse(withoutCausedByMatcher.group(2)), null);
		}
		return null;
	}

	/**
	 * Get the last missing requirement error in the caused by chain.
	 * @return the missing requirement object
	 */
	public MissingRequirementOsgiError getLastMissingRequirementErrorInChain() {
		MissingRequirementOsgiError currentOsgiError = this;
		while (currentOsgiError.getCausedBy() != null) {
			if (currentOsgiError.getCausedBy() instanceof MissingRequirementOsgiError) {
				currentOsgiError = (MissingRequirementOsgiError) currentOsgiError.getCausedBy();
			} else {
				break;
			}
		}
		return currentOsgiError;
	}

	/**
	 * Get the requirement.
	 * @return the requirement
	 */
	public Requirement getRequirement() {
		return requirement;
	}

	/**
	 * Get the caused by error.
	 * @return the caused by error
	 */
	public OsgiError getCausedBy() {
		return causedBy;
	}

	@Override
	public String getSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("requirement", getLastMissingRequirementErrorInChain().getRequirement());
		return templateEngine.process("errorinterpreter/solution/missingrequirement", context);
	}

	@Override
	public String getEPSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("requirement", getLastMissingRequirementErrorInChain().getRequirement());
		return templateEngine.process("errorinterpreter/epsolution/missingrequirement", context);
	}

	@Override
	public String toString() {
		return resource + " is missing requirement " + requirement
				+ (causedBy != null ? "<div style=\"color:MediumSeaGreen\">caused by</div>" + causedBy : "");
	}
}
