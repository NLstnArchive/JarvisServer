package com.jarvis.server.utils;

public class ByteUnit {

	public static float bytesToKilobytes(long bytes) {
		return bytes / (float) (1024);
	}

	public static float bytesToMegabytes(long bytes) {
		return bytes / (float) (1024 * 1024);
	}

	public static float bytesToGigabytes(long bytes) {
		return bytes / (float) (1024 * 1024 * 1024);
	}

	// ////////////////////////////////////////////////////////

	public static long kilobytesToBytes(long kilobytes) {
		return kilobytes * 1024;
	}

	public static float kilobytesToMegabytes(long kilobytes) {
		return kilobytes / (float) 1024;
	}

	public static float kilobytesToGigabytes(long kilobytes) {
		return kilobytes / (float) (1024 * 1024);
	}

	// ////////////////////////////////////////////////////////

	public static float megabytesToBytes(long megabytes) {
		return megabytes / (float) (1024 * 1024);
	}

	public static float megabytesToKilobytes(long megabytes) {
		return megabytes / (float) 1024;
	}

	public static long megaBytesToGigabytes(long megabytes) {
		return megabytes * 1024;
	}

	// //////////////////////////////////////////////////////

	public static long gigabytesToBytes(long gigabytes) {
		return gigabytes / (1024 * 1024 * 1024);
	}

	public static long gigabytesToKilobytes(long gigabytes) {
		return gigabytes / (1024 * 1024);
	}

	public static long gigabytesToMegabytes(long gigabytes) {
		return gigabytes / 1024;
	}
}
