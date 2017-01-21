package com.jarvis.server.concurrency;

public class JTask {

	String		name;
	boolean		finished;
	boolean		started;
	boolean		running;
	Runnable	task;
	long		lastRun;
	long		schedule	= -1;

	JTask() {
	}

	public void execute() {
		started = true;
		task.run();
		started = false;
		if (schedule == -1)
			finished = true;
	}

	public void stop() {
		running = false;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isScheduled() {
		return schedule != -1;
	}

	public JTask executing(Runnable task) {
		this.task = task;
		return this;
	}

	public JTask runScheduled(long millis) {
		if (millis < 0)
			throw new IllegalArgumentException("Schedule time can't be negative!");
		schedule = millis;
		return this;
	}

	public boolean scheduleExceeded() {
		if (schedule == -1)
			return false;
		else
			return System.currentTimeMillis() - lastRun > schedule;
	}

}
