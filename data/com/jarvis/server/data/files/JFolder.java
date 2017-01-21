package com.jarvis.server.data.files;

import java.io.File;

public class JFolder {

	private File folder;

	JFolder(File folder) {
		this.folder = folder;
	}

	public void create() {
		folder.mkdirs();
	}

	public void deleteWithContent() {
		for (JFolder folder : listFolders())
			folder.deleteWithContent();
		for (JFile file : listFiles())
			file.delete();
	}

	public boolean isEmpty() {
		return folder.list().length == 0;
	}

	public boolean exists() {
		return folder.exists();
	}

	public JFile[] listFiles() {
		File[] internalFiles = folder.listFiles();
		JFile[] files = new JFile[internalFiles.length];
		int fileCount = 0;
		for (int i = 0; i < internalFiles.length; i++) {
			if (!internalFiles[i].isDirectory()) {
				files[i] = new JFile(internalFiles[i]);
				fileCount++;
			}
		}
		JFile[] result = new JFile[fileCount];
		System.arraycopy(files, 0, result, 0, fileCount);
		return files;
	}

	public JFolder[] listFolders() {
		File[] internalFiles = folder.listFiles();
		JFolder[] folders = new JFolder[internalFiles.length];
		int folderCount = 0;
		for (int i = 0; i < internalFiles.length; i++) {
			if (internalFiles[i].isDirectory()) {
				folders[i] = new JFolder(internalFiles[i]);
				folderCount++;
			}
		}
		JFolder[] result = new JFolder[folderCount];
		System.arraycopy(folders, 0, result, 0, folderCount);
		return result;
	}

	public String getPath() {
		return folder.getAbsolutePath();
	}

}
