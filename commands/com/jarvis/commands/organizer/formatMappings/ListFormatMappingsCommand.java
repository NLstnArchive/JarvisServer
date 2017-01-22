package com.jarvis.commands.organizer.formatMappings;

import com.jarvis.commands.Command;
import com.jarvis.server.data.DataPool;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class ListFormatMappingsCommand extends Command {

	public ListFormatMappingsCommand() {
		super("listFormatMappings");
	}

	public boolean checkArgs() {
		if (args.length != 0) {
			Logger.error("Command " + getName() + " requires no arguments!", Level.LVL1);
			return false;
		}
		return true;
	}

	public Runnable compile() {
		Runnable runnable = null;
		runnable = new Runnable() {
			public void run() {
				StringBuilder builder = new StringBuilder();
				builder.append("OrganizerFormatMappings:");
				for (String key : DataPool.getOrganizerFormatMappings().keySet()) {
					builder.append(FileSystem.newLine()).append("\t\t\t\t\t\t").append(key).append("\t").append(DataPool.getOrganizerFormatMappings().get(key));
				}
				Logger.info(builder.toString(), Level.LVL1);
			}
		};
		return runnable;
	}

	public String[] getHelp() {
		return new String[] { "Usage: listFormatMappings", "Lists all active formatMappings" };
	}

}
