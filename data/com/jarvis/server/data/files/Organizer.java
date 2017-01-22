package com.jarvis.server.data.files;

import com.jarvis.server.data.DataPool;
import com.jarvis.server.data.fileFormat.Mp3AudioFile;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class Organizer {

	// FIX [Organizer] some empty folders dont get deleted
	public void organizeFolder(JFolder folder) {
		Logger.info("Organizing Folder " + folder.getPath(), Level.LVL2);
		for (JFolder subFolder : folder.listFolders()) {
			organizeFolder(subFolder);
		}
		for (JFile file : folder.listFiles()) {
			organizeFile(file);
		}
		// JFile[] root = folder.listFiles();
		// List<File> files = new ArrayList<File>();
		// List<File> folders = new ArrayList<File>();
		// for (JFile file : root) {
		// if (file.isDirectory())
		// folders.add(file);
		// else
		// files.add(file);
		// }
		// for (File file : files)
		// organizeFile(file);
		// for (File f : folders) {
		// organizeFolder(f);
		// }
		// if (folder.listFiles().length == 0)
		// folder.delete();
		if (folder.isEmpty())
			folder.deleteWithContent();
	}

	private void organizeFile(JFile f) {
		Logger.info("Organizing file " + f.getPath(), Level.LVL2);
		String fileExtension = f.getFileFormat();
		String newFolder = DataPool.getOrganizerFormatMappings().get(fileExtension);
		if (fileExtension.equals(".mp3") && newFolder != null) {
			Mp3AudioFile mp3File = new Mp3AudioFile(f);
			JFolder albumFolder = FileSystem.getFolder(newFolder + mp3File.getArtist() + "/" + mp3File.getAlbum() + "/");
			albumFolder.create();
			Logger.info("Successfully created new Folder: " + albumFolder.getPath(), Level.LVL4);
			f.move(albumFolder);
		}
		else
			if (newFolder != null)
				f.move(FileSystem.getFolder(newFolder));
	}

}
