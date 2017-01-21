package com.jarvis.server.data;

import com.jarvis.server.JarvisModule;
import com.jarvis.server.utils.Logger.Logger;
import com.jarvis.server.utils.Logger.Logger.Level;

public class DataModule extends JarvisModule {

	public DataModule() {
		super("DataModule");
	}

	public void preInit() {

	}

	public void init() {

	}

	public void postInit() {

		Logger.info("DataModule initialized!", Level.LVL1);
	}

	public void shutDown() {

	}

}
