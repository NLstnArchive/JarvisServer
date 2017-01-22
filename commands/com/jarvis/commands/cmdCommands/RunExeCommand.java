package com.jarvis.commands.cmdCommands;

import com.jarvis.commands.Command;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.data.files.JFile;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;
import com.jarvis.server.utils.StreamGobbler;

public class RunExeCommand extends Command {

	// FIX [RunExeCommand][Medium] add support for whitespace in paths
	public RunExeCommand() {
		super("runExe");
	}

	public Runnable compile() {
		Logger.info("Starting to compile Command!", Level.LVL3);
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					JFile executable = FileSystem.getFile((String) args[0]);
					Process p = null;
					try {
						p = new ProcessBuilder().command("cmd", "/K", executable.getPath()).start();
						Logger.info("Executing command!", Level.LVL2);
					}
					catch (Exception e) {
						Logger.error("Failed to create Process!", Level.LVL1);
						e.printStackTrace();
						return;
					}
					StreamGobbler infoGobbler = new StreamGobbler(p.getInputStream());
					StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream());

					infoGobbler.start();
					errorGobbler.start();
					Logger.info("Successfully started exe " + executable.getName(), Level.LVL1);
				}
			};
		}
		catch (Exception e) {
			Logger.error("Failed to compile command runExe!", Level.LVL1);
		}
		return runnable;
	}

	public boolean checkArgs() {
		if (args.length != 1) {
			Logger.error("The command " + getName() + " requires exactly 1 argument!", Level.LVL1);
			return false;
		}
		if (!isString(args[0])) {
			Logger.error("Command " + getName() + " requires a String as argument!", Level.LVL1);
			return false;
		}
		else {
			if (FileSystem.getFile((String) args[0]).canExecute())
				return true;
			else {
				Logger.error("Command " + getName() + " requires a runnable file as argument!", Level.LVL1);
				return false;
			}
		}

	}

	public String[] getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
}