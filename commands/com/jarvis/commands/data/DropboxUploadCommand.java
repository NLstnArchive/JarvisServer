package com.jarvis.commands.data;

import com.jarvis.commands.Command;
import com.jarvis.server.JarvisServer;
import com.jarvis.server.data.DropboxHandler;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.data.files.JFile;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class DropboxUploadCommand extends Command {

	public DropboxUploadCommand() {
		super("dbxUpload");
	}

	public Runnable compile() {
		Runnable runnable = null;
		try {
			runnable = new Runnable() {
				public void run() {
					JFile sourceFile = FileSystem.getFile((String) args[0]);

					DropboxHandler dropbox = JarvisServer.getDataModule().getDropboxHandler();
					if (dropbox.uploadFile(sourceFile, (String) args[1]))
						Logger.info("Uploaded File " + sourceFile + " to " + (String) args[1], Level.LVL1);
				}
			};
		}
		catch (Exception e) {
			Logger.error("Failed to compile Command " + getName(), Level.LVL1);
		}
		return runnable;
	}

	public boolean checkArgs() {
		if (args.length != 2) {
			Logger.error("Command " + getName() + " requires exactly 2 arguments", Level.LVL1);
			return false;
		}
		String sourceFile = null;
		try {
			sourceFile = (String) args[0];
		}
		catch (Exception e) {
			Logger.error("Command " + getName() + " requires Strings as first argument!", Level.LVL1);
			return false;
		}
		if (!isString(args[1])) {
			Logger.error("Command " + getName() + " requires a String as second argument!", Level.LVL1);
			return false;
		}
		if (FileSystem.getFile(sourceFile).exists())
			return true;

		else {
			Logger.error("Command " + getName() + " requires an existing file to upload!", Level.LVL1);
			return false;
		}
	}

	public String[] getHelp() {
		return new String[] { "Usage: dbxUpload <localPath> <remotePath>", "Uploads the file specified by the <localpath>, which gets translated by Jarvis' FileSystem, ", " to the <remotePath> in the dropbox." };
	}
}
