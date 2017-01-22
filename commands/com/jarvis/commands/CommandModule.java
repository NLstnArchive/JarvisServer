package com.jarvis.commands;

import com.jarvis.server.JarvisModule;

public class CommandModule extends JarvisModule {

	private CommandBuilder commandBuilder;

	public CommandModule() {
		super("Command");
	}

	public void preInit() {

	}

	public void init() {
		running = true;
		commandBuilder = new CommandBuilder();
		commandBuilder.init();
	}

	public void postInit() {

	}

	public void shutDown() {
		running = false; // TODO handle running automatically
	}

	public CommandBuilder getCommandBuilder() {
		return commandBuilder;
	}

}
