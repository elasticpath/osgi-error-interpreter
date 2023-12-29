package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses resource strings.
 */
public class Resource {
	private static final Pattern PATTERN_1 = Pattern.compile("([\\w\\.-]+) \\[(\\d+)]\\(R ([\\d\\.]+)\\)");
	private static final Pattern PATTERN_2 = Pattern.compile("classpath:([^|]+)\\|bnd.id=([^|]+)\\|bnd.sym=([^|]+)");
	private final String symbolicName;
	private final int bundleId;

	/**
	 * Constructor.
	 * @param symbolicName the symbolic name
	 * @param bundleId the bundle ID
	 */
	public Resource(final String symbolicName, final int bundleId) {
		this.symbolicName = symbolicName;
		this.bundleId = bundleId;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static Resource parse(final String string) {
		Matcher matcher1 = PATTERN_1.matcher(string);
		if (matcher1.matches()) {
			return new Resource(matcher1.group(1), Integer.parseInt(matcher1.group(2)));
		}
		Matcher matcher2 = PATTERN_2.matcher(string);
		if (matcher2.matches()) {
			return new Resource(matcher2.group(3), Integer.parseInt(matcher2.group(2)));
		}
		return new Resource("Unknown", 0);
	}

	/**
	 * Get the symbolic name.
	 * @return the symbolic name
	 */
	public String getSymbolicName() {
		return symbolicName;
	}

	@Override
	public String toString() {
		return "<div>Bundle with symbolic name <span style=\"color:DarkOrange\">" + symbolicName
				+ "</span> and bundle ID <span style=\"color:DarkOrange\">" + bundleId + "</span></div>";
	}
}
