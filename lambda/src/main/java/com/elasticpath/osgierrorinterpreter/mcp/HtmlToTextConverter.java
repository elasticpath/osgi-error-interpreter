package com.elasticpath.osgierrorinterpreter.mcp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

/**
 * Converts the interpreter's HTML output into plain text suitable for LLM consumption.
 */
public final class HtmlToTextConverter {

	private HtmlToTextConverter() {
		// Prevent instantiation of this utility class.
	}

	/**
	 * Convert an HTML fragment to plain text, preserving anchor URLs as "text (url)".
	 * @param html the HTML to convert
	 * @return the plain text representation, or an empty string if the input is null or blank
	 */
	public static String convert(final String html) {
		if (html == null || html.trim().isEmpty()) {
			return "";
		}
		FormattingVisitor visitor = new FormattingVisitor();
		NodeTraversor.traverse(visitor, Jsoup.parse(html).body());
		return visitor.toString()
				.replaceAll("[ \\t]{2,}", " ")
				.replaceAll("[ \\t]*\\n[ \\t]*", "\n")
				.replaceAll("\\n{3,}", "\n\n")
				.trim();
	}

	/**
	 * Jsoup node visitor that accumulates plain text with basic block-level formatting.
	 */
	private static final class FormattingVisitor implements NodeVisitor {
		private static final String NEWLINE = "\n";
		private final StringBuilder accumulator = new StringBuilder();

		@Override
		public void head(final Node node, final int depth) {
			if (node instanceof TextNode) {
				accumulator.append(((TextNode) node).text());
			} else if ("li".equals(node.nodeName())) {
				accumulator.append(NEWLINE).append("- ");
			}
		}

		@Override
		public void tail(final Node node, final int depth) {
			String name = node.nodeName();
			if ("a".equals(name)) {
				String href = node.attr("href");
				if (!href.isEmpty()) {
					accumulator.append(" (").append(href).append(")");
				}
			} else if ("p".equals(name) || "div".equals(name) || "br".equals(name)) {
				accumulator.append(NEWLINE);
			}
		}

		@Override
		public String toString() {
			return accumulator.toString();
		}
	}
}
