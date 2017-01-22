package com.jarvis.server.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.jarvis.server.data.files.FileSystem;

public class Logger {

	// FUTURE [Logger] report exceptions to JarvisServer.
	private Logger() {

	}

	public static enum Level {
		LVL1(1), LVL2(2), LVL3(3), LVL4(4);

		private int level;

		Level(int level) {
			this.level = level;
		}

		public int get() {
			return level;
		}
	}

	public static void init() {
		lvl1Writer = new BufferedWriter(FileSystem.getFile("jarvisHome/logs/lvl1.log").genFileWriter());
		lvl2Writer = new BufferedWriter(FileSystem.getFile("jarvisHome/logs/lvl2.log").genFileWriter());
		lvl3Writer = new BufferedWriter(FileSystem.getFile("jarvisHome/logs/lvl3.log").genFileWriter());
		lvl4Writer = new BufferedWriter(FileSystem.getFile("jarvisHome/logs/lvl4.log").genFileWriter());
		if (lvl1Writer != null && lvl2Writer != null && lvl3Writer != null && lvl4Writer != null)
			initialized = true;
	}

	private static int				LEVEL		= 4;

	private static BufferedWriter	lvl1Writer;
	private static BufferedWriter	lvl2Writer;
	private static BufferedWriter	lvl3Writer;
	private static BufferedWriter	lvl4Writer;

	private static boolean			initialized	= false;

	public static void info(String message, Level level) {
		if (!initialized) {
			System.err.println("Logger is not initialized!");
			return;
		}
		if (message == null)
			return;
		if (level.get() <= LEVEL) {
			String callerClassName = getCallerClassName();
			message.replace("\n", FileSystem.newLine());
			String timestamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
			StringBuilder builder = new StringBuilder();
			builder.append("[").append(timestamp).append("][").append(callerClassName).append("]");
			for (int i = 0; i < calcTabSpace(callerClassName); i++) {
				builder.append("\t");
			}
			builder.append(message);
			String msg = builder.toString();
			System.out.println(msg);
			try {
				if (level.get() < 5) {
					lvl4Writer.write(msg);
					lvl4Writer.newLine();
				}
				if (level.get() < 4) {
					lvl3Writer.write(msg);
					lvl3Writer.newLine();
				}
				if (level.get() < 3) {
					lvl2Writer.write(msg);
					lvl2Writer.newLine();
				}
				if (level.get() < 2) {
					lvl1Writer.write(msg);
					lvl1Writer.newLine();
				}
				close();
			}
			catch (IOException e) {
				// PLANNING [Logger] how should this be logged?
				Logger.error("Failed to write to LoggerWriter: " + e.getMessage(), Level.LVL1);
			}
		}
	}

	// PLANNING [Logger] do i need the level here? or should all errors be top level? tending to no -> minor errors that dont matter but should be logged for sending to JarvisServer maybe.
	public static void error(String message, Level level) {
		if (!initialized) {
			System.err.println("Logger is not initialized!");
			return;
		}
		if (message == null)
			return;
		if (level.get() <= LEVEL) {
			String callerClassName = getCallerClassName();
			message.replace("\n", FileSystem.newLine());
			String timestamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
			StringBuilder builder = new StringBuilder();
			builder.append("[").append(timestamp).append("][").append(callerClassName).append("]");
			for (int i = 0; i < calcTabSpace(callerClassName); i++) {
				builder.append("\t");
			}
			builder.append(message);
			String msg = builder.toString();
			System.err.println(msg);
			try {
				if (level.get() > 3) {
					lvl4Writer.write(msg);
					lvl4Writer.newLine();
				}
				if (level.get() > 2) {
					lvl3Writer.write(msg);
					lvl3Writer.newLine();
				}
				if (level.get() > 1) {
					lvl2Writer.write(msg);
					lvl2Writer.newLine();
				}
				lvl1Writer.write(msg);
				lvl1Writer.newLine();
				close();
			}
			catch (IOException e) {
				// PLANNING [Logger] how should this be logged?
				Logger.error("Failed to write to LoggerWriter: " + e.getMessage(), Level.LVL1);
			}
		}
	}

	public static void setLoggerLevel(int level) {
		if (level > 4 || level < 1) {
			error("You can't specify a loggerlevel greater than 4 or less than 1!", Level.LVL1);
			return;
		}
		Logger.LEVEL = level;
	}

	public static void close() {
		try {
			lvl1Writer.flush();
			lvl2Writer.flush();
			lvl3Writer.flush();
			lvl4Writer.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getCallerClassName() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
				String fileName = ste.getFileName();
				if (fileName == null)
					return "UnknownFileName";
				fileName = fileName.substring(0, fileName.length() - 5);
				return fileName;
			}
		}
		return null;
	}

	private static int calcTabSpace(String className) {
		if (className.length() >= 28)
			return 1;
		if (className.length() >= 20)
			return 2;
		if (className.length() >= 11)
			return 3;
		if (className.length() >= 6)
			return 4;
		else
			return 5;
	}
}