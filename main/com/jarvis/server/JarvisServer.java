package com.jarvis.server;

import com.jarvis.commands.CommandModule;
import com.jarvis.input.InputModule;
import com.jarvis.server.concurrency.SuperThreadPool;
import com.jarvis.server.data.DataModule;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.net.NetModule;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class JarvisServer {

	private static SuperThreadPool	commonThreadPool;

	private static DataModule		dataModule;
	private static NetModule		netModule;
	private static CommandModule	commandModule;
	private static InputModule		inputModule;

	public final static String		version	= "pre-alpha";

	private JarvisServer() {

	}

	private static void preInit() {
		FileSystem.init();
		Logger.init();
		commonThreadPool = new SuperThreadPool();
		inputModule = new InputModule();
		inputModule.preInit();
		commandModule = new CommandModule();
		commandModule.preInit();
		dataModule = new DataModule();
		dataModule.preInit();
		netModule = new NetModule();
		netModule.preInit();
	}

	private static void init() {
		inputModule.init();
		commandModule.init();
		dataModule.init();
		netModule.init();
	}

	private static void postInit() {
		inputModule.postInit();
		commandModule.postInit();
		dataModule.postInit();
		netModule.postInit();
	}

	public static void shutdown() {
		inputModule.shutDown();
		commandModule.shutDown();
		dataModule.shutDown();
		netModule.shutDown();
	}

	public static InputModule getInputModule() {
		return inputModule;
	}

	public static CommandModule getCommandModule() {
		return commandModule;
	}

	public static DataModule getDataModule() {
		return dataModule;
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
