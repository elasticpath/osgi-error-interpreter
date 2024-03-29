package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Parses filters that contain "objectClass=".
 */
public class InterfaceClassFilter implements Filter {
	private static final Pattern PATTERN = Pattern.compile("objectClass=([^\\)]+)");
	private final String filterString;
	private final Set<String> interfaceClasses;

	/**
	 * Constructor.
	 * @param filterString the filter string
	 * @param interfaceClasses the interface classes
	 */
	public InterfaceClassFilter(final String filterString, final Set<String> interfaceClasses) {
		this.filterString = filterString;
		this.interfaceClasses = interfaceClasses;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static InterfaceClassFilter parse(final String string) {
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
	public String getMissing(final boolean asHTML) {
		if (interfaceClasses.size() == 1) {
			if (asHTML) {
				return "service for interface <code>" + interfaceClasses.iterator().next() + "</code>";
			} else {
				return "service for interface " + interfaceClasses.iterator().next();
			}
		} else {
			if (asHTML) {
				return "service for interfaces <code>" + StringUtils.join(interfaceClasses, ", ") + "</code>";
			} else {
				return "service for interfaces " + StringUtils.join(interfaceClasses, ",\\n");
			}
		}
	}

	@Override
	public String getMissingRaw() {
		return interfaceClasses.toString();
	}

	@Override
	public String getSolution() {
		return getMissing(true);
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
