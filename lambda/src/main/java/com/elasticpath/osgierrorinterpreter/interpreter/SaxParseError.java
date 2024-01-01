package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Parses errors regarding XML definition errors.
 */
public class SaxParseError implements OsgiError {
	private static final Pattern PATTERN = Pattern.compile("XML document from OSGi resource\\[([^\\]]+)\\] is invalid; nested exception is org.xml"
			+ ".sax.SAXParseException; systemId: (.+); lineNumber: \\d+; columnNumber: \\d+; s4s-elt-character: Non-whitespace characters are not "
			+ "allowed in schema elements other than 'xs:appinfo' and 'xs:documentation'.");

	private final Resource resource;

	private final String schemaFile;

	/**
	 * Constructor.
	 * @param resource the resource
	 * @param schemaFile the schema file
	 */
	public SaxParseError(final Resource resource, final String schemaFile) {
		this.resource = resource;
		this.schemaFile = schemaFile;
	}

	/**
	 * Generate the appropriate object given the inputs.
	 * @param string the string to parse
	 * @return the instantiated object
	 */
	public static SaxParseError parse(final String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new SaxParseError(Resource.parse(matcher.group(1)), matcher.group(2));
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
	 * Get the schema file.
	 * @return the schema file
	 */
	public String getSchemaFile() {
		return schemaFile;
	}

	@Override
	public String getErrorInterpretation() {
		return "<div>Bundle with symbolic name <span style=\"color:DarkOrange\">" + resource.getSymbolicName()
				+ "</span></div>is missing a <code>spring.schemas</code> file in the classpath that specifies <div style=\"color:DarkOrange\">"
				+ getSchemaFile() + "</div>";
	}

	@Override
	public String getErrorInterpretationDiagram() {
		return null;
	}

	@Override
	public String getSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/solution/saxparse", context);
	}

	@Override
	public String getEPSolutionHtml(final TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/epsolution/saxparse", context);
	}
}
