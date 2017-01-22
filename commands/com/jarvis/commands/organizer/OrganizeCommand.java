package com.jarvis.commands.organizer;

import com.jarvis.commands.Command;
import com.jarvis.server.JarvisServer;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.data.files.JFolder;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class OrganizeCommand extends Command {

	// TODO create FormatMappingSets for different Folders to choose
	public OrganizeCommand() {
		super("organize");
	}

	public Runnable compile() {
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					JFolder folder = FileSystem.getFolder((String) args[0]);
					JarvisServer.getDataModule().getOrganizer().organizeFolder(folder);
				}
			};
		}
		catch (Exception e) {
			Logger.error("Failed to compile Runnable for Command " + getName(), Level.LVL1);
		}
		return runnable;
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
		if (FileSystem.getFolder((String) args[0]) == null) {
			Logger.error("Command " + getName() + " requires a folder as argument!", Level.LVL1);
			return false;
		}
		return true;
	}

	public String[] getHelp() {
		return new String[] { "Usage: organize <localPath>", "Organizes the folder specified by <localPath>. It scans all files in the folder and it's subfolders for datatypes specified in the format mappings and moves them in the mapped folders. Subfolders of <localPath>, that are empty after the organizing, get deleted", "BUGS: Sometimes some folders that are empty after the organizing don't get deleted." };
	}

}
