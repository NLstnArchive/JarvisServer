package com.jarvis.server.data.files;

import java.util.ArrayList;
import java.util.List;

import com.jarvis.server.Cleanable;

public class TempFileHandler implements Cleanable {

	private List<JFile> tempFiles = new ArrayList<JFile>();

	public void cleanUp() {
		for (JFile tempFile : tempFiles)
			tempFile.delete();
	}

	JFile createTempFile(String name) {
		JFile tempFile = FileSystem.getFileAndCreate("temp/" + name + ".tmp");
		tempFiles.add(tempFile);
		return tempFile;
	}

}
