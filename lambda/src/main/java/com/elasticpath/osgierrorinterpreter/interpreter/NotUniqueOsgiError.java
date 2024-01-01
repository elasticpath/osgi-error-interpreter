package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Parses errors regarding non-unique bundles.
 */
public class NotUniqueOsgiError implements OsgiError {
	private static final Pattern PATTERN = Pattern.compile("Bundle symbolic name and version are not unique: ([^:]+):([\\d\\.]+)");

	private final String bundleSymbolicName;
	private final String bundleVersion;

	/**
	 * Constructor.
	 * @param bundleSymbolicName the bundle symbolic name
	 * @param bundleVersion the bundle version
	 */
	public NotUniqueOsgiError(final String bundleSymbolicName, final String bundleVersion) {
		this.bundleSymbolicName = bundleSymbolicName;
		this.bundleVersion = bundleVersion;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static NotUniqueOsgiError parse(final String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new NotUniqueOsgiError(matcher.group(1), matcher.group(2));
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
	 * Get the bundle version.
	 * @return the bundle version
	 */
	public String getBundleVersion() {
		return bundleVersion;
	}

	@Override
	public String getErrorInterpretation() {
		return "<div>Multiple bundles with the same symbolic name <span style=\"color:DarkOrange\">" + bundleSymbolicName + "</span> and version "
				+ "<span style=\"color:DarkOrange\">" + bundleVersion + "</span> are deployed into the OSGi environment.</div>";
	}

	@Override
	public String getErrorInterpretationDiagram() {
		return null;
	}

	@Override
	public String getSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/solution/notunique", context);
	}

	@Override
	public String getEPSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/epsolution/notunique", context);
	}
}
