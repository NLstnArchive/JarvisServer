package com.jarvis.server.concurrency;

import java.util.ArrayList;
import java.util.List;

import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

//FUTURE [SuperThreadPool][Difficult] Add categories to join e.g. all startup threads.
public class SuperThreadPool {

	private static final int	minFreeThreads	= 5;

	private List<JarvisThread>	availableThreads;
	private List<JarvisThread>	usedThreads;

	public SuperThreadPool() {
		availableThreads = new ArrayList<JarvisThread>();
		usedThreads = new ArrayList<JarvisThread>();
		ensureCapacity(10);
	}

	public List<JarvisThread> requestThreads(int count) {
		Logger.info("SuperThreadPool received Thread request. Count: " + count + ",  Capacity: " + availableThreads.size(), Level.LVL2);
		ensureCapacity(count);
		List<JarvisThread> batch = new ArrayList<JarvisThread>();
		for (int i = 0; i < count; i++) {
			JarvisThread thread = availableThreads.remove(0);
			batch.add(thread);
			usedThreads.add(thread);
		}
		Logger.info("Added requested threads to usedThreads. Threads in use: " + usedThreads.size() + ", Capacity: " + availableThreads.size(), Level.LVL4);
		return batch;
	}

	private void ensureCapacity(int count) {
		if ((availableThreads.size() - count) < minFreeThreads) {
			int increase = count + 5 - availableThreads.size();
			Logger.info("Capacity of SuperThreadPool is to low, creating " + increase + " new Threads", Level.LVL2);
			for (int i = 0; i < increase; i++) {
				availableThreads.add(new JarvisThread());
			}
			Logger.info("Increased SuperThreadPool capacity to " + availableThreads.size(), Level.LVL4);
		}
	}

	public void joinAll() {
		Logger.info("Joining all SuperThreadPool threads", Level.LVL2);
		for (JarvisThread thread : usedThreads)
			try {
				Logger.info("Joining Thread " + thread.getName(), Level.LVL4);
				thread.join(500);
			}
			catch (InterruptedException e) {
				Logger.error("Failed to join thread: " + thread, Level.LVL1);
				e.printStackTrace();
			}
	}
}
