package com.jarvis.commands;

import java.util.ArrayList;
import java.util.List;

import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class CommandBuilder {

	private List<Command>	commands	= new ArrayList<Command>();

	private List<String>	subNames	= new ArrayList<String>();

	public void init() {
	}

	public void registerCommand(Command command) {
		boolean invalid = false;
		for (String subName : subNames) {
			if (command.containsName(subName))
				invalid = true;
		}
		if (invalid) {
			Logger.error("Failed to register Command " + command.getName() + ", NameConflictError!", Level.LVL1);
			return;
		}
		else {
			commands.add(command);
		}
	}

	public Runnable buildCommand(String name, Object... args) {
		Logger.info("Trying to build command...", Level.LVL2);
		Command cmd = null;
		for (Command command : commands) {
			if (command.containsName(name))
				cmd = command;
		}
		if (cmd != null) {
			cmd.setArgs(args);
			if (cmd.checkArgs())
				return cmd.compile();
			else {
				for (String message : cmd.getHelp())
					Logger.info(message, Level.LVL1);
				return null;
			}

		}
		else
			return null;
	}
}
