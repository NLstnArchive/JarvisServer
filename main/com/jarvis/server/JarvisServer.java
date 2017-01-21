package com.jarvis.server;

import com.jarvis.server.concurrency.SuperThreadPool;
import com.jarvis.server.data.DataModule;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.net.NetModule;
import com.jarvis.server.utils.Logger.Logger;
import com.jarvis.server.utils.Logger.Logger.Level;

public class JarvisServer {

	private static SuperThreadPool commonThreadPool;

	private static DataModule dataModule;
	private static NetModule netModule;

	private JarvisServer() {

	}

	private static void preInit() {
		FileSystem.init();
		Logger.init();
		commonThreadPool = new SuperThreadPool();
		dataModule = new DataModule();
		dataModule.preInit();
		netModule = new NetModule();
		netModule.preInit();
	}

	private static void init() {
		dataModule.init();
		netModule.init();
	}

	private static void postInit() {
		dataModule.postInit();
		netModule.postInit();
	}

	public static void shutdown() {

	}

	public static NetModule getNetModule() {
		return netModule;
	}

	public static SuperThreadPool getCommonThreadPool() {
		return commonThreadPool;
	}

	public static void main(String[] args) {
		JarvisServer.preInit();
		Logger.info("Starting JarvisServer!", Level.LVL1);
		JarvisServer.init();
		JarvisServer.postInit();
		Logger.info("Finished starting JarvisServer!", Level.LVL1);
	}

}
