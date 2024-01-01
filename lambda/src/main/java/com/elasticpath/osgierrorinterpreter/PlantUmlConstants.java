package com.elasticpath.osgierrorinterpreter;

/**
 * Constants used for PlantUML diagram construction.
 */
public final class PlantUmlConstants {
	/**
	 * Private constructor.
	 */
	private PlantUmlConstants() {
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
}
