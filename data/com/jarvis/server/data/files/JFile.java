package com.jarvis.server.data.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class JFile {

	private File internalFile;

	JFile(File f) {
		internalFile = f;
	}

	public void delete() {
		internalFile.delete();
	}

	public String getName() {
		return internalFile.getName();
	}

	public FileReader genFileReader() {
		if (!exists()) {
			Logger.error("Can't create FileReader for non existing file '" + getPath() + "'!", Level.LVL1);
			return null;
		}
		try {
			return new FileReader(internalFile);
		}
		catch (IOException e) {
			Logger.error("Failed to create FileReader for file '" + getPath() + "'!", Level.LVL1);
			return null;
		}
	}

	public FileWriter genFileWriter() {
		if (!exists()) {
			Logger.info("Creating new file for '" + internalFile.getAbsolutePath() + "'", Level.LVL1);
			try {
				internalFile.createNewFile();
			}
			catch (IOException e) {
				Logger.error("Failed to create missing file for FileWriter '" + getPath() + "': " + e.getMessage(), Level.LVL1);
				return null;
			}
			return null;
		}
		try {
			return new FileWriter(internalFile);
		}
		catch (IOException e) {
			Logger.error("Failed to open FileWriter for file '" + internalFile + "', because: " + e.getMessage(), Level.LVL1);
			return null;
		}
	}

	public OutputStream genOutputStream() {
		if (!exists()) {
			Logger.error("Can't create OutputStream for non existing file '" + internalFile.getAbsolutePath() + "'!", Level.LVL1);
			return null;
		}
		try {
			return new FileOutputStream(internalFile);
		}
		catch (Exception e) {
			Logger.error("Failed to open OutputStream foor file '" + getPath() + "', because " + e.getMessage(), Level.LVL1);
			return null;
		}
	}

	public FileInputStream genFileInputStream() {
		if (!exists()) {
			Logger.error("Can't create FileInputStream for non existing file '" + internalFile.getAbsolutePath() + "'!", Level.LVL1);
			return null;
		}
		try {
			return new FileInputStream(internalFile);
		}
		catch (FileNotFoundException e) {
			Logger.error("Failed to open FileInputStream for file '" + internalFile.getAbsolutePath() + "'!", Level.LVL1);
			return null;
		}
	}

	public void create() {
		try {
			internalFile.createNewFile();
		}
		catch (IOException e) {
			Logger.error("Failed to create file at '" + getPath() + "', cause: " + e.getMessage(), Level.LVL1);
			e.printStackTrace();
		}
	}

	public void move(JFolder newFolder) {
		File newFile = new File(newFolder + "/" + internalFile.getName());
		int i = 1;
		while (newFile.exists()) {
			String fileName = newFile.getName().substring(0, newFile.getName().lastIndexOf("."));
			String fileExtension = newFile.getName().substring(newFile.getName().lastIndexOf("."));
			Logger.info("Found existing file in folder " + newFolder + " of name " + fileName + " with extension " + fileExtension, Level.LVL3);
			newFile = new File(newFolder + "/" + fileName + " (" + i++ + ")" + fileExtension);
			Logger.info("Trying to rename file to " + newFile.getName(), Level.LVL3);
		}
		Logger.info("Moving file " + getPath() + " to " + newFile.getAbsolutePath(), Level.LVL4);
		if (internalFile.renameTo(newFile))
			Logger.info("Successfully moved file to " + getPath(), Level.LVL4);
	}

	public byte[] toStream() {
		byte[] result = new byte[getStreamSize()];
		result[0] = '/';
		String newPath = getPath().replaceAll("/", ";");
		byte[] file = toBytes();
		System.arraycopy(newPath, 0, result, 1, newPath.length());
		System.arraycopy(file, 0, result, newPath.length() + 1, file.length);
		result[result.length - 1] = '/';
		return result;
	}

	public byte[] toBytes() {
		try {
			return Files.readAllBytes(Paths.get(internalFile.getPath()));
		}
		catch (IOException e) {
			Logger.error("Failed to convert file '" + getPath() + "' to bytes! " + e.getMessage(), Level.LVL1);
			return null;
		}
	}

	public int getStreamSize() {
		StringBuilder builder = new StringBuilder();
		builder.append("/").append(getPath().replaceAll("/", ";")).append("/");
		return (int) (builder.toString().getBytes().length + internalFile.length());
	}

	public Date getLastModified() {
		return new Date(internalFile.lastModified());
	}

	public String getFileFormat() {
		return internalFile.getAbsolutePath().substring(internalFile.getAbsolutePath().lastIndexOf("."));
	}

	public String getPath() {
		return internalFile.getAbsolutePath();
	}

	public boolean canExecute() {
		return internalFile.canExecute();
	}

	public boolean exists() {
		return internalFile.exists();
	}

	public long getSize() {
		return internalFile.length();
	}

	public static JFile fromStream(byte[] data) {
		String metaData = new String(data);
		String[] parts = metaData.split("/");
		String path = parts[1];
		int startFile = path.getBytes().length + 2;
		byte[] fileData = new byte[data.length - startFile];
		System.arraycopy(data, startFile, fileData, 0, fileData.length);
		File newFile = new File(path);
		try {
			FileOutputStream stream = new FileOutputStream(newFile);
			stream.write(fileData);
			stream.flush();
			stream.close();
		}
		catch (FileNotFoundException e) {
			Logger.error("Failed to save file from network. Path not specified! " + path, Level.LVL1);
			return null;
		}
		catch (IOException e) {
			Logger.error("Failed to write data from network to file! " + e.getMessage(), Level.LVL1);
			return null;
		}
		return new JFile(newFile);
	}
}
