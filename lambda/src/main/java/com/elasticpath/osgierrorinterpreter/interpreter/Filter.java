package com.elasticpath.osgierrorinterpreter.interpreter;

/**
 * An interface for classes that parse filter strings.
 */
public interface Filter {
	/**
	 * Get the type of filter.
	 * @return the type of filter
	 */
	String getType();

	/**
	 * Get the description of what is missing.
	 * @param asHtml return response in HTML format
	 * @return the description of what is missing
	 */
	String getMissing(boolean asHtml);

	/**
	 * Get a simple description of what is missing.
	 * @return the simple description of what is missing
	 */
	String getMissingRaw();

	/**
	 * Get the suggested solution to resolve the missing filter.
	 * @return the suggested solution to resolve the missing filter
	 */
	String getSolution();

	/**
	 * Get the raw filter string.
	 * @return the raw filter string
	 */
	String getFilterString();

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	static Filter parse(final String string) {
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
		ExecutionEnvironmentFilter eeFilter = ExecutionEnvironmentFilter.parse(string);
		if (eeFilter != null) {
			return eeFilter;
		}
		return UnknownFilter.parse(string);
	}
}
