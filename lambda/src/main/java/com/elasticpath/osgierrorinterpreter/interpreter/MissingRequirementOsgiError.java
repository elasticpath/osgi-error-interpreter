package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.elasticpath.osgierrorinterpreter.PlantUmlConstants;

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
	public String getErrorInterpretation() {
		return resource + " is missing requirement " + requirement
				+ (causedBy != null ? "<div style=\"color:MediumSeaGreen\">caused by</div>" + causedBy.getErrorInterpretation() : "");
	}

	@Override
	public String getErrorInterpretationDiagram() {
		StringBuilder diagram = new StringBuilder();
		diagram.append(PlantUmlConstants.HEADER);
		diagram.append(generateDiagramBundle(requirement.getResource().getSymbolicName(), requirement.getFilter().getMissing(false), null));
		MissingRequirementOsgiError previousError = this;
		while (previousError.getCausedBy() instanceof MissingRequirementOsgiError) {
			MissingRequirementOsgiError currentError = (MissingRequirementOsgiError) previousError.getCausedBy();
			diagram.append(generateDiagramBundle(currentError.getRequirement().getResource().getSymbolicName(),
					currentError.getRequirement().getFilter().getMissing(false),
					previousError.getRequirement().getFilter().getMissing(false)));
			diagram.append("[Imports\\n").append(previousError.getRequirement().getFilter().getMissing(false))
					.append("] --> [Exports\\n").append(previousError.getRequirement().getFilter().getMissing(false)).append("]\n");
			previousError = currentError;
		}
		diagram.append(generateDiagramMissingBundle(previousError.getRequirement().getFilter().getMissing(false)));
		diagram.append("[Imports\\n").append(previousError.getRequirement().getFilter().getMissing(false))
				.append("] --> [Exports\\n").append(previousError.getRequirement().getFilter().getMissing(false)).append("]\n");
		diagram.append(PlantUmlConstants.FOOTER);
		return diagram.toString();
	}

	private String generateDiagramBundle(final String bundleId, final String imports, final String exports) {
		StringBuilder diagram = new StringBuilder();
		diagram.append("package \"Bundle ").append(bundleId).append("\" {\n");
		if (exports != null) {
			diagram.append("    component \"Exports\\n").append(exports).append("\"\n");
		}
		if (imports != null) {
			diagram.append("    component \"Imports\\n").append(imports).append("\"\n");
		}
		if (imports != null && exports != null) {
			diagram.append("    [Exports\\n").append(exports).append("] -[hidden]-> [").append("Imports\\n")
					.append(imports).append("]\n");
		}
		diagram.append("}\n");
		return diagram.toString();
	}

	private String generateDiagramMissingBundle(final String exports) {
		StringBuilder diagram = new StringBuilder();
		diagram.append("package \"Missing Bundle\" #salmon {\n");
		diagram.append("    component \"Exports\\n").append(exports).append("\"\n");
		diagram.append("}\n");
		return diagram.toString();
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
}
