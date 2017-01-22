package com.jarvis.input;

import com.jarvis.server.JarvisServer;

public class InputRequestor {

	private volatile String nextInput;

	public String waitForNextInput() {
		JarvisServer.getInputModule().getInputDispatcher().requestInput(this);
		while (nextInput == null)
			try {
				Thread.sleep(20); // PERFO [InputRequestor] check how high the sleep time can be before it gets clunky.
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		String input = nextInput;
		nextInput = null;
		return input;
	}

	public void requestInput(String input) {
		nextInput = input;
	}

}
