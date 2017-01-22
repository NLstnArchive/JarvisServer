package com.jarvis.commands.organizer.formatMappings;

import com.jarvis.commands.Command;
import com.jarvis.input.InputRequestor;
import com.jarvis.server.data.DataPool;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class ClearFormatMappings extends Command {

	public ClearFormatMappings() {
		super("clearFormatMappings");
	}

	private static InputRequestor inputRequestor = new InputRequestor();

	public boolean checkArgs() {
		if (args.length != 0) {
			Logger.error("Command " + getName() + " requires no argument!", Level.LVL1);
			return false;
		}
		return true;
	}

	public Runnable compile() {
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					Logger.info("Do you really want to clear the formatMappings? (Yes/No)", Level.LVL1);
					String answer = inputRequestor.waitForNextInput();
					if (answer.equalsIgnoreCase("yes")) {
						DataPool.getOrganizerFormatMappings().clear();
						Logger.info("Successfully cleared OrganizerFormatMappings.", Level.LVL1);
					}
					else
						if (answer.equalsIgnoreCase("no")) {
							return;
						}
						else {
							Logger.error("Unexpected input: " + answer, Level.LVL1);
							return;
						}
				}
			};
		}
		catch (Exception e) {
			Logger.error("Failed to compile runnable! " + e.getMessage(), Level.LVL1);
		}
		return runnable;
	}

	public String[] getHelp() {
		return new String[] { "Usage: clearFormatMappings", "Clears all formatMappings" };
	}

}
