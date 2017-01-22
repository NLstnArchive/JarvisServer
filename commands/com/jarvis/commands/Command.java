package com.jarvis.commands;

public abstract class Command {

	private String		name;

	private String[]	subNames;

	protected Object[]	args;

	public Command(String name, String... subNames) {
		this.name = name;
		this.subNames = subNames;
		args = new Object[0];
	}

	public String[] getSubNames() {
		return subNames;
	}

	public void setArgs(Object... args) {
		this.args = args;
	}

	public String getName() {
		return name;
	}

	public boolean containsName(String name) {
		if (this.name.equals(name))
			return true;
		for (String subName : subNames) {
			if (subName.equals(name))
				return true;
		}
		return false;
	}

	public abstract boolean checkArgs();

	public abstract Runnable compile();

	public abstract String[] getHelp();

	protected boolean isString(Object object) {
		return object instanceof String;
	}

	protected boolean isStringArray(Object object) {
		return object instanceof String[];
	}

	protected boolean isInt(Object object) {
		return object instanceof Integer;
	}
}