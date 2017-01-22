package com.jarvis.commands.cmdCommands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jarvis.commands.Command;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class CmdCommand extends Command {

	public CmdCommand() {
		super("cmd");
	}

	public Runnable compile() {
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					Runtime rt = Runtime.getRuntime();
					Process p = null;
					try {
						p = rt.exec((String[]) args);
					}
					catch (IOException e) {
						Logger.error("Failed to create Process.", Level.LVL1);
						e.printStackTrace();
						return;
					}
					try {
						handleInput(p.getInputStream());
						handleInput(p.getErrorStream());
					}
					catch (IOException e) {
						Logger.error("Failed to receive Input from Process: " + args[0], Level.LVL1);
						return;
					}

					Logger.info("Successfully issued command!", Level.LVL1);
				}
			};
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return runnable;
	}

	private void handleInput(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		String line = "";
		while ((line = reader.readLine()) != null) {
			Logger.info(line, Level.LVL1);
		}
		reader.close();
	}

	public boolean checkArgs() {
		if (args.length == 0) {
			Logger.error("The command " + getName() + " requires at least 1 argument!", Level.LVL1);
			return false;
		}
		if (isStringArray(args)) {
			return true;
		}
		else {
			Logger.error("Command " + getName() + " requires a String array as argument", Level.LVL1);
			return false;
		}
	}

	public String[] getHelp() {
		return new String[] { "Usage: cmd <command>", "Executes a standard windows command and outputs the result to the jarvis console." };
	}
}