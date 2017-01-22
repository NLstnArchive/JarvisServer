package com.jarvis.server.data.fileFormat;

import java.io.IOException;

import com.jarvis.input.InputRequestor;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.data.files.JFile;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Mp3AudioFile {

	private JFile					f;
	private String					title;
	private String					artist;
	private String					album;
	private long					length;

	private static InputRequestor	requestor	= new InputRequestor();

	// the new data is not visible in the .mp3 file in windows, but will be recognized by jarvis
	public Mp3AudioFile(String f) {
		this.f = FileSystem.getFile(f);
		read();
	}

	public Mp3AudioFile(JFile f) {
		this.f = f;
		read();
	}

	private void read() {
		Mp3File songData;
		try {
			songData = new Mp3File(f.getPath());
			length = songData.getLengthInSeconds();
			if (songData.hasId3v2Tag()) {
				ID3v2 songTags = songData.getId3v2Tag();

				title = songTags.getTitle();
				artist = songTags.getArtist();
				album = songTags.getAlbum();

				Logger.info("MP3 file " + f + " has ID3v2 tags!", Level.LVL4);
			}
			boolean needsChanges = false;
			if (songData.hasId3v1Tag()) {
				ID3v1 songTags = songData.getId3v1Tag();

				title = songTags.getTitle();
				artist = songTags.getArtist();
				album = songTags.getAlbum();
				Logger.info("MP3 file " + f + " has ID3v1 tags!", Level.LVL4);
				needsChanges = true;
			}
			if (title == null || title.equals("")) {
				Logger.info("MP3 file " + f.getPath() + " is missing a title. Please specify one!", Level.LVL1);
				title = requestor.waitForNextInput();
				Logger.info("New title of this MP3: " + title, Level.LVL1);
				needsChanges = true;
			}
			if (artist == null) {
				Logger.info("This MP3 file is missing an artist. Please specify one!", Level.LVL1);
				artist = requestor.waitForNextInput();
				Logger.info("New artist of this MP3: " + artist, Level.LVL1);
				needsChanges = true;
			}
			if (album == null) {
				Logger.info("This MP3 file is missing an album. Please specify one!", Level.LVL1);
				album = requestor.waitForNextInput();
				Logger.info("New Album is: " + album, Level.LVL1);
				needsChanges = true;
			}
			if (needsChanges) {
				ID3v2 tags = new ID3v24Tag();
				tags.setTitle(title);
				tags.setArtist(artist);
				tags.setAlbum(album);
				JFile tmpFile = FileSystem.getTempFile("temp.mp3");
				songData.save(tmpFile.getPath());
				Mp3File newFile = new Mp3File(tmpFile.getPath());
				newFile.setId3v2Tag(tags);
				newFile.save(f.getPath());
			}
		}
		catch (UnsupportedTagException e) {
			e.printStackTrace();
		}
		catch (InvalidDataException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (NotSupportedException e) {
			e.printStackTrace();
		}

		Logger.info("Successfully read new Mp3 File! Title: " + title + ", Artist: " + artist + ", Album: " + album + ", it is " + length + " seconds long", Level.LVL3);
	}

	public JFile getFile() {
		return f;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public long getLength() {
		return length;
	}

	public static void main(String[] args) {
		new Mp3AudioFile("C:/Users/niklas.lahnstein/Desktop/test.mp3");
	}

}
