package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class TimedOutOsgiError implements OsgiError {
	private static final Pattern PATTERN = Pattern.compile("Application context initialization for '([^']+)' has timed out waiting for (.+)");

	private final String bundleSymbolicName;
	private final Filter filter;

	public TimedOutOsgiError(String bundleSymbolicName, Filter filter) {
		this.bundleSymbolicName = bundleSymbolicName;
		this.filter = filter;
	}

	public static TimedOutOsgiError parse(String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new TimedOutOsgiError(matcher.group(1), Filter.parse(matcher.group(2)));
		}
		return null;
	}

	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}

	public Filter getFilter() {
		return filter;
	}

	@Override
	public String getSolutionHtml(TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/solution/timedout", context);
	}

	@Override
	public String getEPSolutionHtml(TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/epsolution/timedout", context);
	}

	@Override
	public String toString() {
		return "<div>Bundle with symbolic name <span style=\"color:DarkOrange\">" + bundleSymbolicName + "</span></div>timed out waiting for a service that provides <div style=\"color:DarkOrange\">" + filter.getMissing() + "</div>";
	}
}
