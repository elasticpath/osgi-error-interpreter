package com.elasticpath.osgierrorinterpreter.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class SaxParseError implements OsgiError {
	private static final Pattern PATTERN = Pattern.compile("XML document from OSGi resource\\[([^\\]]+)\\] is invalid; nested exception is org.xml.sax.SAXParseException; systemId: (.+); lineNumber: \\d+; columnNumber: \\d+; s4s-elt-character: Non-whitespace characters are not allowed in schema elements other than 'xs:appinfo' and 'xs:documentation'.");

	private final Resource resource;

	private final String schemaFile;

	public SaxParseError(Resource resource, String schemaFile) {
		this.resource = resource;
		this.schemaFile = schemaFile;
	}

	public static SaxParseError parse(String string) {
		Matcher matcher = PATTERN.matcher(string);
		if (matcher.find()) {
			return new SaxParseError(Resource.parse(matcher.group(1)), matcher.group(2));
		}
		return null;
	}

	public Resource getResource() {
		return resource;
	}

	public String getSchemaFile() {
		return schemaFile;
	}

	@Override
	public String getSolutionHtml(TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/solution/saxparse", context);
	}

	@Override
	public String getEPSolutionHtml(TemplateEngine templateEngine) {
		Context context = new Context();
		context.setVariable("error", this);
		return templateEngine.process("errorinterpreter/epsolution/saxparse", context);
	}

	@Override
	public String toString() {
		return "<div>Bundle with symbolic name <span style=\"color:DarkOrange\">" + resource.getSymbolicName() + "</span></div>is missing a <code>spring.schemas</code> file in the classpath that specifies <div style=\"color:DarkOrange\">" + getSchemaFile() + "</div>";
	}
}
