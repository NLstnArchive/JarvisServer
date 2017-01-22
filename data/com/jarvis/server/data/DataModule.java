package com.jarvis.server.data;

import com.jarvis.server.JarvisModule;
import com.jarvis.server.data.files.Organizer;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class DataModule extends JarvisModule {

	private DropboxHandler	dropbox;
	private Organizer		organizer;

	public DataModule() {
		super("DataModule");
	}

	public void preInit() {

	}

	public void init() {
		DataPool.init();
		dropbox = new DropboxHandler();
		organizer = new Organizer();
	}

	public void postInit() {
		Logger.info("DataModule initialized!", Level.LVL1);
	}

	public void shutDown() {
		DataPool.save();
	}

	public DropboxHandler getDropboxHandler() {
		return dropbox;
	}

	public Organizer getOrganizer() {
		return organizer;
	}

}
