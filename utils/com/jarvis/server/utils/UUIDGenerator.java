package com.jarvis.server.utils;

import java.util.ArrayList;
import java.util.List;

public class UUIDGenerator {


	private List<Integer>	uuids	= new ArrayList<Integer>();

	private int				size;

	public UUIDGenerator() {
		for (size = 0; size < 100; size++) {
			uuids.add(size);
		}
	}

	public int getUUID() {
		if (uuids.size() <= 10)
			for (int i = 0; i < 100; i++)
				uuids.add(size++);
		return uuids.remove(0);
	}

	public void returnID(int id) {
		uuids.add(id);
	}
	
}
