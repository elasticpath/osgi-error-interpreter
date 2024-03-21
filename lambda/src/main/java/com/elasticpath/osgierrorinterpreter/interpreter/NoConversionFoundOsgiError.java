package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Parses errors regarding no conversion found errors.
 */
public class NoConversionFoundOsgiError implements OsgiError {
	private static final Pattern PATTERN = Pattern.compile("No conversion found for generic argument\\(s\\) for reified type");

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static NoConversionFoundOsgiError parse(final String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new NoConversionFoundOsgiError();
		}
		return null;
	}

	@Override
	public String getErrorInterpretation() {
		return "<div><code>SpringBlueprintConverter</code> has not been configured.</div>";
	}

	@Override
	public String getErrorInterpretationDiagram() {
		return "";
	}

	@Override
	public String getSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/solution/noconversionfound", context);
	}

	@Override
	public String getEPSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/epsolution/noconversionfound", context);
	}
}
