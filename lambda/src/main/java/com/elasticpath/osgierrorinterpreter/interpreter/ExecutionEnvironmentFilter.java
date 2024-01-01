package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses filters that contain "osgi.ee=".
 */
public class ExecutionEnvironmentFilter implements Filter {
	private static final Pattern PATTERN = Pattern.compile("osgi\\.ee=([^\\)]+)");
	private final String filterString;
	private final String executionEnvironmentName;

	/**
	 * Constructor.
	 * @param filterString the filter string
	 * @param executionEnvironmentName the contract name
	 */
	public ExecutionEnvironmentFilter(final String filterString, final String executionEnvironmentName) {
		this.filterString = filterString;
		this.executionEnvironmentName = executionEnvironmentName;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static ExecutionEnvironmentFilter parse(final String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new ExecutionEnvironmentFilter(string, matcher.group(1));
		}
		return null;
	}

	@Override
	public String getMissing(final boolean asHTML) {
		if (asHTML) {
			return "execution environment <code>" + executionEnvironmentName + "</code>";
		} else {
			return "execution environment " + executionEnvironmentName;
		}
	}

	@Override
	public String getMissingRaw() {
		return executionEnvironmentName;
	}

	@Override
	public String getSolution() {
		return "Java VM compatible with " + getMissing(true);
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}

	@Override
	public String getFilterString() {
		return filterString;
	}
}
