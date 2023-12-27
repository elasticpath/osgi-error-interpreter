package com.elasticpath.osgierrorinterpreter.interpreter;

public class UnknownFilter implements Filter {
	private final String filterString;

	public UnknownFilter(String filterString) {
		this.filterString = filterString;
	}

	public static UnknownFilter parse(String string) {
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
