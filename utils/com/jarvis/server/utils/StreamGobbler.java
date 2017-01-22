package com.jarvis.server.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {

	private InputStream is;

	public StreamGobbler(InputStream stream) {
		this.is = stream;
	}

	public void run() {
		try {
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader bReader = new BufferedReader(reader);
			while (bReader.readLine() != null) {
				continue;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}