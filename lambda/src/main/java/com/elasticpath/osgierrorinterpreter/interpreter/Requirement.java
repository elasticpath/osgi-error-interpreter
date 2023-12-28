package com.elasticpath.osgierrorinterpreter.interpreter;

/**
 * An interface for classes that parse requirement strings.
 */
public interface Requirement {
	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	static Requirement parse(final String string) {
		WrappedRequirement wrappedRequirement = WrappedRequirement.parse(string);
		if (wrappedRequirement != null) {
			return wrappedRequirement;
		}
		return null;
	}
}
