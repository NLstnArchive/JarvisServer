package com.jarvis.commands.organizer.formatMappings;

import com.jarvis.commands.Command;
import com.jarvis.server.data.DataPool;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class RemoveFormatMappingCommand extends Command {

	public RemoveFormatMappingCommand() {
		super("removeFormatMapping");
	}

	public boolean checkArgs() {
		if (args.length != 1) {
			Logger.error("Command " + getName() + " requires exactly 1 argument!", Level.LVL1);
			return false;
		}
		if (!isString(args[0])) {
			Logger.error("Command " + getName() + " requires a String as argument!", Level.LVL1);
			return false;
		}
		return true;
	}

	public Runnable compile() {
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					String arg = (String) args[0];
					if (!DataPool.getOrganizerFormatMappings().containsKey(arg)) {
						Logger.info("No mapping found for key " + arg + " to remove.", Level.LVL1);
						return;
					}
					String value = DataPool.getOrganizerFormatMappings().get(arg);
					DataPool.getOrganizerFormatMappings().remove(arg);
					Logger.info("Successfully removed mapping: " + arg + "->" + value + ".", Level.LVL1);
				}
			};
		}
		catch (Exception e) {
			Logger.error("Failed to compile runnable! " + e.getMessage(), Level.LVL1);
		}
		return runnable;
	}

	public String[] getHelp() {
		return new String[] { "Usage: removeFormatMapping <format> <folder>", "Removes this mapping from the active formatMappingsList" };
	}

}
