package com.jarvis.commands.data;

import com.jarvis.commands.Command;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class MountFolderShortcutCommand extends Command {

	public MountFolderShortcutCommand() {
		super("mountFolder", "mount", "cd");
	}

	public boolean checkArgs() {
		if (args.length != 2) {
			Logger.error("Command " + getName() + " requires exactly 2 arguments!", Level.LVL1);
			return false;
		}
		if (!isString(args[0]) || !isString(args[0])) {
			Logger.error("Command " + getName() + " requires 2 Strings as argument!", Level.LVL1);
			return false;
		}
		if (FileSystem.getFolder((String) args[1]) == null) {
			Logger.error("COmmand " + getName() + " requires an existing folder as second argument!", Level.LVL1);
			return false;
		}
		return true;
	}

	public Runnable compile() {
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					FileSystem.mount((String) args[0], (String) args[1]);
				}
			};
		}
		catch (Exception e) {

		}
		return runnable;
	}

	public String[] getHelp() {
		return new String[] { "Usage: mount <shortCut> <localPath>", "Mounts the specified <localPath> to the <shortCut> being used by the FileSystem to translate paths." };
	}

}
