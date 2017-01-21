package com.jarvis.server.net;

import java.util.concurrent.TimeUnit;

import com.jarvis.server.JarvisModule;
import com.jarvis.server.net.clients.ClientHandler;
import com.jarvis.server.utils.Logger.Logger;
import com.jarvis.server.utils.Logger.Logger.Level;

public class NetModule extends JarvisModule {

	private ClientHandler clientHandler;

	public NetModule() {
		super("NetModule");
	}

	public void preInit() {

	}

	public void init() {
		running = true;
		clientHandler = new ClientHandler();
	}

	public void postInit() {

		Logger.info("Finished initializing NetModule", Level.LVL1);
	}

	public void shutDown() {
		TimeUnit.SECONDS.toDays(10000);
	}

	public ClientHandler getClientHandler() {
		return clientHandler;
	}

}
