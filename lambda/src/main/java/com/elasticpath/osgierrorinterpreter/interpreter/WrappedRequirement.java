package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WrappedRequirement implements Requirement {
	private static final Pattern PATTERN = Pattern.compile("\\[([\\w\\.-]+ \\[\\d+\\]\\(R [\\d\\.]+\\))\\] [\\w\\.]+; ([^\\] ]+)");
	private final Resource resource;
	private final Filter filter;

	public WrappedRequirement(Resource resource, Filter filter) {
		this.resource = resource;
		this.filter = filter;
	}

	public static WrappedRequirement parse(String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new WrappedRequirement(Resource.parse(matcher.group(1)), Filter.parse(matcher.group(2)));
		}
		return null;
	}

	public Resource getResource() {
		return resource;
	}

	public Filter getFilter() {
		return filter;
	}

	@Override
	public String toString() {
		return "<div style=\"color:SlateBlue\">" + filter.getFilterString() + "</div>";
	}
}
