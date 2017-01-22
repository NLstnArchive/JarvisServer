package com.jarvis.input;

import java.util.Scanner;

import com.jarvis.server.JarvisServer;
import com.jarvis.server.concurrency.JTaskBuilder;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class InputDispatcher {

	private Scanner			scanner;

	private Thread			thread;

	private InputRequestor	requestor;

	public InputDispatcher() {
		Logger.info("Setting up CommandDispatcherThreadPool", Level.LVL2);
		scanner = new Scanner(System.in);
		thread = new Thread("CommandDispatcherThread") {
			public void run() {
				while (JarvisServer.getInputModule().isRunning())
					if (scanner.hasNextLine()) {
						if (requestor == null)
							processCommand(scanner.nextLine().trim());
						else {
							requestor.requestInput(scanner.nextLine());
							requestor = null;
						}
					}
			}
		};
		thread.start();
	}

	public void requestInput(InputRequestor requestor) {
		this.requestor = requestor;
	}

	// FUTURE [InputDispatcher][Very Difficult] start work on sentence recognizing
	private void processCommand(String line) {
		Logger.info("Received new command! '" + line + "'", Level.LVL2);
		String[] parts = line.split(" ");
		String[] args = new String[parts.length - 1];
		System.arraycopy(parts, 1, args, 0, args.length);
		Runnable runnable = JarvisServer.getCommandModule().getCommandBuilder().buildCommand(parts[0], (Object[]) args);
		if (runnable == null) {
			Logger.error("Command '" + line + "' could not be compiled!", Level.LVL2);
			return;
		}
		JarvisServer.getInputModule().getThreadPool().submit(JTaskBuilder.newTask().executing(runnable));
	}

}
