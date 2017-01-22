package com.jarvis.commands.organizer.formatMappings;

import com.jarvis.commands.Command;
import com.jarvis.server.data.DataPool;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.data.files.JFolder;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class AddFormatMappingCommand extends Command {

	public AddFormatMappingCommand() {
		super("addFormatMapping");
	}

	public boolean checkArgs() {
		if (args.length != 2) {
			Logger.error("Command " + getName() + " requires exactly 2 arguments!", Level.LVL1);
			return false;
		}
		if (!isString(args[0]) || !isString(args[1])) {
			Logger.error("Command " + getName() + " requires 2 Strings as argument!", Level.LVL1);
			return false;
		}
		if (!((String) args[0]).startsWith(".")) {
			Logger.error("Command " + getName() + " requires a file extension as first argument! " + (String) args[0], Level.LVL1);
			return false;
		}
		JFolder folder = FileSystem.getFolder((String) args[1]);
		if (folder != null) {
			if (!folder.exists()) {
				Logger.error("Command " + getName() + " requires an existing folder as second argument! " + (String) args[1], Level.LVL1);
				return false;
			}
			return true;
		}
		return false;
	}

	public Runnable compile() {
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					String arg1 = (String) args[0];
					String arg2 = (String) args[1];
					DataPool.getOrganizerFormatMappings().put(arg1, arg2);
					Logger.info("Succesfully added new FormatMapping: " + arg1 + "->" + arg2, Level.LVL1);
				}
			};
		}
		catch (Exception e) {
			Logger.error("Failed to compile runnable! " + e.getMessage(), Level.LVL1);
		}
		return runnable;
	}

	public String[] getHelp() {
		return new String[] { "Usage: addFormatMapping <format> <folder>", "Adds this mapping to the formatMappingsList" };
	}

}
