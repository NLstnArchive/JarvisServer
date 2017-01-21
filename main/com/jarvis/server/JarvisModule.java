package com.jarvis.server;

import com.jarvis.server.concurrency.ThreadPool;

public abstract class JarvisModule {

	private String name;

	protected volatile boolean running;

	private ThreadPool threadPool;

	public JarvisModule(String name) {
		this.name = name;
	}

	private final ThreadPool generateThreadPool() {
		return new ThreadPool(name, this);
	}

	/*
	 * Initialize things that are not dependant on other modules, but are needed by other modules.
	 */
	public abstract void preInit();

	// initialize main things
	public abstract void init();

	/*
	 * Initialize things that are dependant on other modules, but are not needed by modules for their main init part.
	 */
	public abstract void postInit();

	// set running = false
	public abstract void shutDown();

	public final ThreadPool getThreadPool() {
		if (threadPool == null)
			threadPool = generateThreadPool();
		return threadPool;
	}

	public final boolean isRunning() {
		return running;
	}

	public final String getName() {
		return name;
	}

}
