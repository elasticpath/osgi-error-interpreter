package com.elasticpath.osgierrorinterpreter.interpreter;

/**
 * Parses unrecognized requirement strings.
 */
public class UnknownRequirement implements Requirement {
	private final String requirementString;

	/**
	 * Constructor.
	 * @param requirementString the requirement string
	 */
	public UnknownRequirement(final String requirementString) {
		this.requirementString = requirementString;
	}

	@Override
	public Resource getResource() {
		return new Resource("Unknown", 0);
	}

	@Override
	public Filter getFilter() {
		return new UnknownFilter("Unknown");
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static UnknownRequirement parse(final String string) {
		return new UnknownRequirement(string);
	}

	@Override
	public String toString() {
		return "<div style=\"color:SlateBlue\">" + requirementString + "</div>";
	}
}
