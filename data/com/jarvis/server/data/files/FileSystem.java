package com.jarvis.server.data.files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jarvis.server.utils.Logger.Logger;
import com.jarvis.server.utils.Logger.Logger.Level;

public class FileSystem {

	private static Map<String, JFolder>	shortCuts;
	private static TempFileHandler		tempFileHandler;

	public static void init() {
		shortCuts = new HashMap<String, JFolder>();
		mount("jarvisHome", "C:/JarvisServer/");
		mount("desktop", System.getProperty("user.home") + "/Desktop/");
		mount("temp", "C:/Jarvis/temp/");
		mount("data", "C:/Jarvis/data/");
		tempFileHandler = new TempFileHandler();
	}

	private FileSystem() {

	}

	// IMPROVEMENT [FileSystem][Medium] evaluate shortcuts even when mounting (e.g. "jarvisHome/tmp")
	public static void mount(String shortCut, String path) {
		if (!shortCuts.containsKey(shortCut))
			shortCuts.put(shortCut, getFolder(path));
	}

	static File getInternalFile(String path) {
		String[] split = path.split("/");
		for (String shortcut : shortCuts.keySet()) {
			if (split[0].equals(shortcut)) {
				path = path.replace(split[0], shortCuts.get(shortcut).getPath());
			}
		}
		// path = path.replace("/", "\\");
		return new File(path);
	}

	public static JFile getFileAndCreate(String path) {
		JFile f = getFile(path);
		f.create();
		return f;
	}

	public static JFile getFile(String path) {
		File f = getInternalFile(path);
		if (f == null) {
			Logger.error("Failed to evaluate path '" + path + "'", Level.LVL1);
			return null;
		}
		if (f.isDirectory()) {
			Logger.error("File " + path + " is a folder, not a file.", Level.LVL1);
			return null;
		}
		return new JFile(f);
	}

	public static JFolder getFolder(String path) {
		if (!path.endsWith("/")) {
			Logger.error("Path " + path + " is not a folder path!", Level.LVL1);
			return null;
		}
		File f = getInternalFile(path);
		if (f == null) {
			Logger.error("Failed to evaluate path '" + path + "'", Level.LVL1);
			return null;
		}
		boolean created = false;
		try {
			created = f.createNewFile();
		}
		catch (IOException e) {
			Logger.error("Failed to create new Folder '" + path + "', " + e.getMessage(), Level.LVL1);
			return null;
		}
		if (!f.isDirectory()) {
			Logger.error("File " + path + " is a file, not a folder.", Level.LVL1);
			if (created)
				f.delete();
			return null;
		}
		return new JFolder(f);
	}

	public static JFile getTempFile(String name) {
		return tempFileHandler.createTempFile(name);
	}

	public static String newLine() {
		return System.getProperty("line.separator");
	}

	public static Map<String, JFolder> getShortcuts() {
		return shortCuts == null ? (shortCuts = new HashMap<>()) : shortCuts;
	}
}