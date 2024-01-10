package com.elasticpath.osgierrorinterpreter;

/**
 * Constants used for PlantUML diagram construction.
 */
public final class PlantUmlUtil {
	/**
	 * Private constructor.
	 */
	private PlantUmlUtil() {
		// No op
	}

	public static final String HEADER = "@startuml\n"
			+ "skin rose\n"
			+ "skinparam componentStyle rectangle\n"
			+ "skinparam component {\n"
			+ "  BackgroundColor DodgerBlue\n"
			+ "  FontColor White\n"
			+ "}\n"
			+ "skinparam package {\n"
			+ "  BackgroundColor Application\n"
			+ "}\n"
			+ "set namespaceSeparator none\n";

	public static final String FOOTER = "@enduml\n";

	/**
	 * Generate a PlantUML fragment to define a bundle.
	 * @param bundleSymbolicName the bundle symbolic name
	 * @param imports what the bundle imports (or null)
	 * @param exports what the bundle exports (or null)
	 * @return the PlantUML fragment
	 */
	public static String generateDiagramBundle(final String bundleSymbolicName, final String imports, final String exports) {
		StringBuilder diagram = new StringBuilder();
		diagram.append("package \"Bundle ").append(bundleSymbolicName).append("\" {\n");
		if (exports != null) {
			diagram.append("    component \"Exports\\n").append(exports).append("\"\n");
		}
		if (imports != null) {
			diagram.append("    component \"Imports\\n").append(imports).append("\"\n");
		}
		if (imports != null && exports != null) {
			diagram.append("    [Exports\\n").append(exports).append("] -[hidden]-> [").append("Imports\\n")
					.append(imports).append("]\n");
		}
		diagram.append("}\n");
		return diagram.toString();
	}

	/**
	 * Generate a PlantUML fragment to define a missing bundle.
	 * @param exports what the bundle should export
	 * @return the PlantUML fragment
	 */
	public static String generateDiagramMissingBundle(final String exports) {
		StringBuilder diagram = new StringBuilder();
		diagram.append("package \"Missing Bundle\" #salmon {\n");
		diagram.append("    component \"Exports\\n").append(exports).append("\"\n");
		diagram.append("}\n");
		return diagram.toString();
	}

}
