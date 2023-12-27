package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class MissingRequirementOsgiError implements OsgiError {
	private static final Pattern WITH_CAUSED_BY_PATTERN = Pattern.compile("Unable to resolve ([^:]+): missing requirement (.+?) \\[caused by: (.+)\\]");
	private static final Pattern WITHOUT_CAUSED_BY_PATTERN = Pattern.compile("Unable to resolve ([^:]+): missing requirement (.+)");

	private final Resource resource;
	private final Requirement requirement;
	private final OsgiError causedBy;

	public MissingRequirementOsgiError(Resource resource, Requirement requirement, OsgiError causedBy) {
		this.resource = resource;
		this.requirement = requirement;
		this.causedBy = causedBy;
	}

	public static MissingRequirementOsgiError parse(String string) {
		Matcher withCausedByMatcher = WITH_CAUSED_BY_PATTERN.matcher(string);
		if (withCausedByMatcher.find()) {
			return new MissingRequirementOsgiError(Resource.parse(withCausedByMatcher.group(1)), Requirement.parse(withCausedByMatcher.group(2)), MissingRequirementOsgiError.parse(withCausedByMatcher.group(3)));
		}
		Matcher withoutCausedByMatcher = WITHOUT_CAUSED_BY_PATTERN.matcher(string);
		if (withoutCausedByMatcher.find()) {
			return new MissingRequirementOsgiError(Resource.parse(withoutCausedByMatcher.group(1)), Requirement.parse(withoutCausedByMatcher.group(2)), null);
		}
		return null;
	}

	public MissingRequirementOsgiError getLastMissingRequirementErrorInChain() {
		MissingRequirementOsgiError currentOsgiError = this;
		while (currentOsgiError.getCausedBy() != null) {
			if (currentOsgiError.getCausedBy() instanceof MissingRequirementOsgiError) {
				currentOsgiError = (MissingRequirementOsgiError) currentOsgiError.getCausedBy();
			}
		}
		return currentOsgiError;
	}

	public Requirement getRequirement() {
		return requirement;
	}

	public OsgiError getCausedBy() {
		return causedBy;
	}

	@Override
	public String getSolutionHtml(TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("requirement", ((WrappedRequirement) getLastMissingRequirementErrorInChain().getRequirement()));
		return templateEngine.process("errorinterpreter/solution/missingrequirement", context);
	}

	@Override
	public String getEPSolutionHtml(TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("requirement", ((WrappedRequirement) getLastMissingRequirementErrorInChain().getRequirement()));
		return templateEngine.process("errorinterpreter/epsolution/missingrequirement", context);
	}

	@Override
	public String toString() {
		return resource + " is missing requirement " + requirement + (causedBy != null ? "<div style=\"color:MediumSeaGreen\">caused by</div>" + causedBy : "");
	}
}
