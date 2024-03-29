package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.elasticpath.osgierrorinterpreter.PlantUmlUtil;

/**
 * Parses errors regarding timeout waiting for services.
 */
public class TimedOutOsgiError implements OsgiError {
	private static final Pattern PATTERN = Pattern.compile("Application context initialization for '([^']+)' has timed out waiting for (.+)");

	private final String bundleSymbolicName;
	private final Filter filter;

	/**
	 * Constructor.
	 * @param bundleSymbolicName the bundle symbolic name
	 * @param filter the filter
	 */
	public TimedOutOsgiError(final String bundleSymbolicName, final Filter filter) {
		this.bundleSymbolicName = bundleSymbolicName;
		this.filter = filter;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static TimedOutOsgiError parse(final String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new TimedOutOsgiError(matcher.group(1), Filter.parse(matcher.group(2)));
		}
		return null;
	}

	/**
	 * Get the bundle symbolic name.
	 * @return the bundle symbolic name
	 */
	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}

	/**
	 * Get the filter.
	 * @return the filter
	 */
	public Filter getFilter() {
		return filter;
	}

	@Override
	public String getErrorInterpretation() {
		return "<div>Bundle with symbolic name <span style=\"color:DarkOrange\">" + bundleSymbolicName + "</span></div>timed out waiting for a "
				+ "<div style=\"color:DarkOrange\">" + filter.getMissing(true) + "</div>";
	}

	@Override
	public String getErrorInterpretationDiagram() {
		StringBuilder diagram = new StringBuilder();
		diagram.append(PlantUmlUtil.HEADER);
		diagram.append(PlantUmlUtil.generateDiagramBundle(bundleSymbolicName, getFilter().getMissing(false), null));
		diagram.append(PlantUmlUtil.generateDiagramMissingBundle(getFilter().getMissing(false)));
		diagram.append("[Imports\\n").append(getFilter().getMissing(false))
				.append("] --> [Exports\\n").append(getFilter().getMissing(false)).append("]\n");
		diagram.append(PlantUmlUtil.FOOTER);
		return diagram.toString();
	}

	@Override
	public String getSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/solution/timedout", context);
	}

	@Override
	public String getEPSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/epsolution/timedout", context);
	}
}
