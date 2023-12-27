package com.elasticpath.osgierrorinterpreter.interpreter;

public interface Filter {
	String getType();

	String getMissing();

	String getMissingRaw();

	String getSolution();

	String getFilterString();

	static Filter parse(String string) {
		InterfaceClassFilter interfaceClassFilter = InterfaceClassFilter.parse(string);
		if (interfaceClassFilter != null) {
			return interfaceClassFilter;
		}
		PackageFilter packageFilter = PackageFilter.parse(string);
		if (packageFilter != null) {
			return packageFilter;
		}
		ContractFilter contractFilter = ContractFilter.parse(string);
		if (contractFilter != null) {
			return contractFilter;
		}
		return UnknownFilter.parse(string);
	}
}
