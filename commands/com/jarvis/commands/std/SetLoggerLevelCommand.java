package com.jarvis.commands.std;

import com.jarvis.commands.Command;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class SetLoggerLevelCommand extends Command {

	public SetLoggerLevelCommand() {
		super("setLoggerLevel");
	}

	public boolean checkArgs() {
		if (args.length != 1) {
			Logger.error("Command " + getName() + " requires exactly 1 argument!", Level.LVL1);
			return false;
		}
		if (!isInt(args[0])) {
			Logger.error("Command " + getName() + " requires an int as argument.", Level.LVL1);
			return false;
		}
		int newLevel = (Integer) args[0];
		if (newLevel > 4 || newLevel < 1) {
			Logger.error("Can only specify LoggerLevel between 4 and 1", Level.LVL1);
			return false;
		}
		return true;
	}

	public Runnable compile() {
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					Logger.setLoggerLevel((Integer) args[0]);
					Logger.info("Changed logger level to " + args[0], Level.LVL1);
				}
			};
		}
		catch (Exception e) {
			Logger.error("Failed to compile Runnable for Command " + getName(), Level.LVL1);
		}
		return runnable;
	}

	public String[] getHelp() {
		return new String[] { "Usage: setLoggerLevel <level>", "Sets the logger level" };
	}

}