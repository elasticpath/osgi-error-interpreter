package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class NotUniqueOsgiError implements OsgiError {
	private static final Pattern PATTERN = Pattern.compile("Bundle symbolic name and version are not unique: ([^:]+):([\\d\\.]+)");

	private final String bundleSymbolicName;
	private final String bundleVersion;


	public NotUniqueOsgiError(String bundleSymbolicName, String bundleVersion) {
		this.bundleSymbolicName = bundleSymbolicName;
		this.bundleVersion = bundleVersion;
	}

	public static NotUniqueOsgiError parse(String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new NotUniqueOsgiError(matcher.group(1), matcher.group(2));
		}
		return null;
	}

	public String getBundleSymbolicName() {
		return bundleSymbolicName;
	}

	public String getBundleVersion() {
		return bundleVersion;
	}

	@Override
	public String getSolutionHtml(TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/solution/notunique", context);
	}

	@Override
	public String getEPSolutionHtml(TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/epsolution/notunique", context);
	}

	@Override
	public String toString() {
		return "<div>Multiple bundles with the same symbolic name <span style=\"color:DarkOrange\">" + bundleSymbolicName + "</span> and version <span style=\"color:DarkOrange\">" + bundleVersion + "</span> are deployed into the OSGi environment.</div>";
	}
}
