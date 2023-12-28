package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses wrapped requirement strings.
 * See https://github.com/snefru/org.apache.felix.resolver/blob/master/src/main/java/org/apache/felix/resolver/WrappedRequirement.java.
 */
public class WrappedRequirement implements Requirement {
	private static final Pattern PATTERN = Pattern.compile("\\[([\\w\\.-]+ \\[\\d+\\]\\(R [\\d\\.]+\\))\\] [\\w\\.]+; ([^\\] ]+)");
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
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new WrappedRequirement(Resource.parse(matcher.group(1)), Filter.parse(matcher.group(2)));
		}
		return null;
	}

	/**
	 * Get the resource.
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Get the filter.
	 * @return the filter
	 */
	public Filter getFilter() {
		return filter;
	}

	@Override
	public String toString() {
		return "<div style=\"color:SlateBlue\">" + filter.getFilterString() + "</div>";
	}
}
