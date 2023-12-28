package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses filters that contain "osgi.wiring.package=".
 */
public class PackageFilter implements Filter {
	private static final Pattern PATTERN = Pattern.compile("osgi\\.wiring\\.package=([^\\)]+)");
	private final String filterString;
	private final String packageName;

	/**
	 * Constructor.
	 * @param filterString the filter string
	 * @param packageName the package name
	 */
	public PackageFilter(final String filterString, final String packageName) {
		this.filterString = filterString;
		this.packageName = packageName;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static PackageFilter parse(final String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new PackageFilter(string, matcher.group(1));
		}
		return null;
	}

	@Override
	public String getMissing() {
		return "package <code>" + packageName + "</code>";
	}

	@Override
	public String getMissingRaw() {
		return packageName;
	}

	@Override
	public String getSolution() {
		return "bundle that exports " + getMissing();
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
