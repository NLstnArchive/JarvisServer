package com.jarvis.server.concurrency;

import com.jarvis.server.JarvisModule;
import com.jarvis.server.utils.Logger.Logger;
import com.jarvis.server.utils.Logger.Logger.Level;

public class JarvisThread extends Thread {

	private JarvisModule	module;

	private JTask			task;

	private ThreadPool		currentThreadPool;

	public JarvisThread() {
		super("IdleThread");
	}

	public JarvisThread(String name) {
		super(name);
	}

	public void assignModule(String name, JarvisModule module, ThreadPool pool) {
		this.module = module;
		this.currentThreadPool = pool;
		setName(name);
	}

	public void setTask(JTask task) {
		this.task = task;
	}

	public void run() {
		if (task == null)
			Logger.error("No Runnable to execute! " + getName(), Level.LVL1);
		else {
			task.execute();
		}
		currentThreadPool.returnThread(this);
		Logger.info("Returned finished jarvisThread to threadPool!", Level.LVL4);
	}

	public JarvisModule getModule() {
		return module;
	}
}
