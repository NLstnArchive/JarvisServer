package com.jarvis.server.concurrency;

import java.util.ArrayList;
import java.util.List;

import com.jarvis.server.JarvisModule;
import com.jarvis.server.JarvisServer;
import com.jarvis.server.data.UniqueArrayList;

public class ThreadPool {

	private static List<ThreadPool>	threadPools	= new ArrayList<ThreadPool>();

	private static boolean			running;

	private static Thread			updateThread;

	static {
		recreateThread();
	}

	private static void registerThreadPool(ThreadPool pool) {
		running = false;
		try {
			updateThread.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		threadPools.add(pool);
		running = true;
		recreateThread();
	}

	private static void recreateThread() {
		updateThread = new Thread("ThreadPoolUpdaterThread") {
			public void run() {
				while (running) {
					for (ThreadPool pool : threadPools)
						pool.update();
				}
				try {
					Thread.sleep(250);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		updateThread.start();
	}

	private List<JarvisThread>	availableThreads;

	private List<JTask>			pendingTasks;

	private List<JTask>			scheduledTasks;

	private JarvisModule		module;

	public ThreadPool(String name, JarvisModule module) {
		pendingTasks = new UniqueArrayList<JTask>();
		availableThreads = new UniqueArrayList<JarvisThread>();
		scheduledTasks = new UniqueArrayList<JTask>();
		registerThreadPool(this);
		this.module = module;
	}

	public void update() {
		while (pendingTasks.size() > 0 && availableThreads.size() > 0) {
			startNewTask();
		}
		if (pendingTasks.size() > 0 && availableThreads.size() == 0) {
			requestThreads(pendingTasks.size() + 5);
		}
		checkScheduledTasks();
	}

	private void startNewTask() {
		JTask task = pendingTasks.remove(0);
		JarvisThread thread = availableThreads.remove(0);
		thread.setTask(task);
		thread.start();
		if (task.isScheduled()) {
			task.lastRun = System.currentTimeMillis();
			scheduledTasks.add(task);
		}
	}

	private void checkScheduledTasks() {
		List<JTask> toBeExecuted = new UniqueArrayList<JTask>();
		for (JTask task : scheduledTasks) {
			if (task.scheduleExceeded())
				toBeExecuted.add(task);
		}
		for (JTask task : toBeExecuted) {
			scheduledTasks.remove(task);
			pendingTasks.add(task);
		}
	}

	public void requestThreads(int count) {
		List<JarvisThread> threads = JarvisServer.getCommonThreadPool().requestThreads(count);
		for (JarvisThread thread : threads) {
			thread.assignModule(module.getName(), module, this);
			availableThreads.add(thread);
		}
	}

	public void returnThread(JarvisThread thread) {
		thread.setTask(null);
		availableThreads.add(thread);
	}

	public void submit(JTask task) {
		if (task.isScheduled())
			scheduledTasks.add(task);
		else
			pendingTasks.add(task);
	}
}