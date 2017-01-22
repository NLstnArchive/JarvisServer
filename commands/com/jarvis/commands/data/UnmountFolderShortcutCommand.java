package com.jarvis.commands.data;

import com.jarvis.commands.Command;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class UnmountFolderShortcutCommand extends Command {

	public UnmountFolderShortcutCommand() {
		super("unmount");
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
					String key = (String) args[0];
					String value = FileSystem.getShortcuts().remove(key).getPath();
					if (value != null)
						Logger.info("Successfully unmounted FolderShortcut " + key + "->" + value, Level.LVL1);
					else
						Logger.info("No FolderShortcut found for key " + key, Level.LVL1);
				}
			};
		}
		catch (Exception e) {
			Logger.error("Failed to compile runnable for command " + getName() + "!", Level.LVL1);
		}
		return runnable;
	}

	public String[] getHelp() {
		return new String[] { "Usage: unmount <shortCut> <localPath>", "Unmounts the specified <localPath> from the <shortCut>" };
	}

}
