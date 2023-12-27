package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterfaceClassFilter implements Filter {
	private static final Pattern PATTERN = Pattern.compile("objectClass=([^\\)]+)");
	private final String filterString;
	private final Set<String> interfaceClasses;

	public InterfaceClassFilter(String filterString, Set<String> interfaceClasses) {
		this.filterString = filterString;
		this.interfaceClasses = interfaceClasses;
	}

	public static InterfaceClassFilter parse(String string) {
		Set<String> interfaceClassesInternal = new HashSet<>();
		Matcher matcher = PATTERN.matcher(string);
		while (matcher.find()) {
			interfaceClassesInternal.add(matcher.group(1));
		}
		if (interfaceClassesInternal.isEmpty()) {
			return null;
		} else {
			return new InterfaceClassFilter(string, interfaceClassesInternal);
		}
	}

	@Override
	public String getMissing() {
		if (interfaceClasses.size() == 1) {
			return "interface <code>" + interfaceClasses.iterator().next() + "</code>";
		} else {
			return "interfaces <code>" + interfaceClasses + "</code>";
		}
	}

	@Override
	public String getMissingRaw() {
		return interfaceClasses.toString();
	}

	@Override
	public String getSolution() {
		return "service for " + getMissing();
	}

	@Override
	public String getFilterString() {
		return filterString;
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return filterString;
	}
}
