package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses filters that contain "osgi.contract=".
 */
public class ContractFilter implements Filter {
	private static final Pattern PATTERN = Pattern.compile("osgi\\.contract=([^\\)]+)");
	private final String filterString;
	private final String contractName;

	/**
	 * Constructor.
	 * @param filterString the filter string
	 * @param contractName the contract name
	 */
	public ContractFilter(final String filterString, final String contractName) {
		this.filterString = filterString;
		this.contractName = contractName;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static ContractFilter parse(final String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new ContractFilter(string, matcher.group(1));
		}
		return null;
	}

	@Override
	public String getMissing() {
		return "contract <code>" + contractName + "</code>";
	}

	@Override
	public String getMissingRaw() {
		return contractName;
	}

	@Override
	public String getSolution() {
		return "bundle that provides " + getMissing();
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
