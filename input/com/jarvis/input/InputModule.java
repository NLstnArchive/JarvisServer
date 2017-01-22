package com.jarvis.input;

import com.jarvis.server.JarvisModule;

public class InputModule extends JarvisModule {

	private InputDispatcher inputDispatcher;

	public InputModule() {
		super("InputModule");
	}

	public void preInit() {

	}

	public void init() {
		running = true;
		inputDispatcher = new InputDispatcher();
	}

	public void postInit() {

	}

	public void shutDown() {
		running = false;
	}

	public InputDispatcher getInputDispatcher() {
		return inputDispatcher;
	}
}
