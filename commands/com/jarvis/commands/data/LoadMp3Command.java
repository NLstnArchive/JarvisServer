package com.jarvis.commands.data;

import com.jarvis.commands.Command;
import com.jarvis.server.data.fileFormat.Mp3AudioFile;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.data.files.JFile;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class LoadMp3Command extends Command {

	public LoadMp3Command() {
		super("loadMp3");
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
		JFile f = FileSystem.getFile((String) args[0]);
		if (!f.exists()) {
			Logger.error("Command " + getName() + " requires an existing file as argument!", Level.LVL1);
			return false;
		}
		if (!f.getFileFormat().equals(".mp3")) {
			Logger.error("Command " + getName() + " requires an mp3 file as argument!", Level.LVL1);
			return false;
		}
		return true;
	}

	public Runnable compile() {
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					new Mp3AudioFile((String) args[0]);
				}
			};
		}
		catch (Exception e) {
			Logger.error("Failed to compile Runnable!", Level.LVL1);
			return null;
		}
		return runnable;
	}

	public String[] getHelp() {
		return new String[] { "Usage: loadMp3 <localPath>", "Loads the file specified by <localPath> into memory and asks for possibly missing ID3 information, which get saved in this file.", "This Command should be used to supply ID3 information for MP3 files", "BUG: new ID3 tags are not visible in Windows Explorer, however Jarvis can read them." };
	}

}
