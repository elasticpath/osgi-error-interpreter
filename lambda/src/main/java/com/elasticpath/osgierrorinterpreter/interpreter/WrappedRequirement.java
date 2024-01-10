package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses wrapped requirement strings.
 * See https://github.com/snefru/org.apache.felix.resolver/blob/master/src/main/java/org/apache/felix/resolver/WrappedRequirement.java.
 */
public class WrappedRequirement implements Requirement {
	private static final Pattern PATTERN_1 = Pattern.compile("\\[([\\w\\.-]+ \\[\\d+\\]\\(R [\\d\\.]+\\))\\] [\\w\\.]+; ([^\\] ]+)");
	private static final Pattern PATTERN_2 = Pattern.compile("\\[([^\\]]+)\\] [\\w\\.]+; ([^\\] ]+)");
	private final Resource resource;
	private final Filter filter;

	/**
	 * Constructor.
	 * @param resource the resource
	 * @param filter the filter
	 */
	public WrappedRequirement(final Resource resource, final Filter filter) {
		this.resource = resource;
		this.filter = filter;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static WrappedRequirement parse(final String string) {
		Matcher matcher1 = PATTERN_1.matcher(string);
		if (matcher1.find()) {
			return new WrappedRequirement(Resource.parse(matcher1.group(1)), Filter.parse(matcher1.group(2)));
		}
		Matcher matcher2 = PATTERN_2.matcher(string);
		if (matcher2.find()) {
			return new WrappedRequirement(Resource.parse(matcher2.group(1)), Filter.parse(matcher2.group(2)));
		}
		return null;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	@Override
	public String toString() {
		return "<div style=\"color:SlateBlue\">" + filter.getFilterString() + "</div>";
	}
}
