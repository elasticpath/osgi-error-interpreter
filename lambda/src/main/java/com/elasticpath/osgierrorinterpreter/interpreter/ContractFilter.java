package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContractFilter implements Filter {
	private static final Pattern PATTERN = Pattern.compile("osgi\\.contract=([^\\)]+)");
	private final String filterString;
	private final String contractName;

	public ContractFilter(String filterString, String contractName) {
		this.filterString = filterString;
		this.contractName = contractName;
	}

	public static ContractFilter parse(String string) {
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
