package com.jarvis.server.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jarvis.server.JarvisServer;
import com.jarvis.server.concurrency.JTask;
import com.jarvis.server.concurrency.JTaskBuilder;
import com.jarvis.server.concurrency.ThreadPool;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.data.files.JFile;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class DataPool {

	// Common class for referencing resources for jarvis in lists

	private static final JFile			organizerFormatMappingsFile		= FileSystem.getFile("data/organizerFormatMappings.dat");
	private static final JFile			fileSystemShortcutMountsFile	= FileSystem.getFile("data/fileSystemShortcutMounts.dat");

	private static Map<String, String>	organizerFormatMappings			= new HashMap<String, String>();							// FileExtension, new Folder

	/**
	 * Load all resources
	 */
	public static void init() {
		@SuppressWarnings("unused")
		long size = calcFileSizes();
		ThreadPool dataThreadPool = JarvisServer.getDataModule().getThreadPool();
		dataThreadPool.submit(loadOrganizerMappings());
		dataThreadPool.submit(loadFileSystemShortcuts());
	}

	private static long calcFileSizes() {
		long size = 0;
		size += organizerFormatMappingsFile.getSize();
		size += fileSystemShortcutMountsFile.getSize();
		return size;
	}

	private static JTask loadOrganizerMappings() {
		return JTaskBuilder.newTask().executing(new Runnable() {
			public void run() {
				try {
					Logger.info("Starting to load resources", Level.LVL1);
					BufferedReader reader = new BufferedReader(organizerFormatMappingsFile.genFileReader());
					String line;
					while ((line = reader.readLine()) != null) {
						String[] split = line.split(";");
						if (split.length == 2) {
							if (split[0].equals("") || split[0] == null || split[1].equals("") || split[1] == null) {
								Logger.error("Read false OrganizerFormatMappings entry: " + line, Level.LVL1);
							}
							else {
								organizerFormatMappings.put(split[0], split[1]);
							}
						}
						else {
							Logger.error("Read false OrganizerFormatMappings entry: " + line, Level.LVL1);
						}
					}
					reader.close();
					Logger.info("Finished loading resources!", Level.LVL1);
				}
				catch (FileNotFoundException e) {
					Logger.error("Failed to localize organizerFormatMappings.dat file!", Level.LVL1);
					e.printStackTrace();
				}
				catch (IOException e) {
					Logger.error("Failed to read from organizerFormatMappings.dat file!", Level.LVL1);
					e.printStackTrace();
				}
			}
		});
	}

	private static JTask loadFileSystemShortcuts() {
		return JTaskBuilder.newTask().executing(new Runnable() {
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(fileSystemShortcutMountsFile.genFileReader());
					String line;
					while ((line = reader.readLine()) != null) {
						String[] split = line.split(";");
						if (split.length != 2) {
							if (split[0].equals("") || split[0] == null || split[1].equals("") || split[1] == null) {
								Logger.error("Read false FileSystemShortcut entry: " + line, Level.LVL1);
							}
							else {
								FileSystem.getShortcuts().put(split[0], FileSystem.getFolder(split[1]));
							}
						}
					}
					reader.close();
				}
				catch (IOException e) {
					Logger.error("Failed to load FileSystemShortcuts from file!", Level.LVL1);
					e.printStackTrace();
				}
			}
		});
	}

	private static void saveFileSystemShortcuts() {
		try {
			FileWriter writer = fileSystemShortcutMountsFile.genFileWriter();
			for (String key : FileSystem.getShortcuts().keySet()) {
				writer.write(key + ";" + FileSystem.getShortcuts().get(key).getPath() + FileSystem.newLine());
			}
			writer.close();
		}
		catch (IOException e) {
			Logger.error("Failed to save FileSystemFormats! " + e.getMessage(), Level.LVL1);
		}
	}

	private static void saveOrganizerMappings() {
		try {
			FileWriter writer = organizerFormatMappingsFile.genFileWriter();
			for (String key : organizerFormatMappings.keySet()) {
				writer.write(key + ";" + organizerFormatMappings.get(key) + FileSystem.newLine());
			}
			writer.close();
		}
		catch (IOException e) {
			Logger.error("Failed to save OrganizerFormatMappings! " + e.getMessage(), Level.LVL1);
			e.printStackTrace();
		}
	}

	public static void save() {
		saveOrganizerMappings();
		saveFileSystemShortcuts();
	}

	public static Map<String, String> getOrganizerFormatMappings() {
		return organizerFormatMappings;
	}
}