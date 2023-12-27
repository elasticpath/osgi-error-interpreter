package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageFilter implements Filter {
	private static final Pattern PATTERN = Pattern.compile("osgi\\.wiring\\.package=([^\\)]+)");
	private final String filterString;
	private final String packageName;

	public PackageFilter(String filterString, String packageName) {
		this.filterString = filterString;
		this.packageName = packageName;
	}

	public static PackageFilter parse(String string) {
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
