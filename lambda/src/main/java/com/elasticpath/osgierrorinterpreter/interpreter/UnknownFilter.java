package com.elasticpath.osgierrorinterpreter.interpreter;

/**
 * Parses unrecognized filter strings.
 */
public class UnknownFilter implements Filter {
	private final String filterString;

	/**
	 * Constructor.
	 * @param filterString the filter string
	 */
	public UnknownFilter(final String filterString) {
		this.filterString = filterString;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static UnknownFilter parse(final String string) {
		return new UnknownFilter(string);
	}

	@Override
	public String getMissing() {
		return "<code>" + filterString + "</code>";
	}

	@Override
	public String getMissingRaw() {
		return filterString;
	}

	@Override
	public String getSolution() {
		return "unknown";
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
