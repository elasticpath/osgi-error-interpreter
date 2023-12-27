package com.elasticpath.osgierrorinterpreter.interpreter;

public interface Requirement {
	static Requirement parse(String string) {
		WrappedRequirement wrappedRequirement = WrappedRequirement.parse(string);
		if (wrappedRequirement != null) {
			return wrappedRequirement;
		}
		return null;
	}
}
