package com.jarvis.server.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.NetworkIOException;
import com.dropbox.core.RetryException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CommitInfo;
import com.dropbox.core.v2.files.DeleteErrorException;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadSessionCursor;
import com.dropbox.core.v2.files.UploadSessionFinishErrorException;
import com.dropbox.core.v2.files.UploadSessionLookupErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.jarvis.server.JarvisServer;
import com.jarvis.server.data.files.FileSystem;
import com.jarvis.server.data.files.JFile;
import com.jarvis.server.utils.ByteUnit;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class DropboxHandler {

	// this determines if uploadSmallFile or uploadChunkedFile is used -> if file is smaller than 2 chunk uploadSmallFile()
	private static final long	CHUNKED_UPLOAD_CHUNK_SIZE	= 100 * 1024;	// IMPROVEMENT [DropboxHandler][Difficult] Figure out how this can be balanced.
	private static final int	CHUNKED_UPLOAD_MAX_ATTEMPTS	= 5;

	private DbxClientV2			client;

	public DropboxHandler() {
		DbxRequestConfig config = new DbxRequestConfig("Jarvis/" + JarvisServer.version);
		client = new DbxClientV2(config, "C5PkaBanyzUAAAAAAAACRL_wFV7Ry1xJ3BgruWXZLOGG6b4pzyT8Z56TClMzbzUK");
	}

	public JFile getFile(String file, JFile save) {
		try (OutputStream out = save.genOutputStream()) {
			FileMetadata metadata = client.files().download(file).download(out);
			System.out.println(metadata.toStringMultiline());
		}
		catch (DownloadErrorException e) {
			e.printStackTrace();
		}
		catch (DbxException e) {
			Logger.error("Failed to download file '" + file + "' from Dropbox. " + e.getMessage(), Level.LVL1);
			return null;
		}
		catch (IOException e) {
			Logger.error("Failed to download file '" + file + "' from Dropbox. " + e.getMessage(), Level.LVL1);
			return null;
		}
		return save;
	}

	public JFile getFile(String file, String save) {
		JFile f = FileSystem.getFile(save);
		return getFile(file, f);
	}

	public JFile getFile(String file) {
		String fileName = "tmp_" + file.substring(file.lastIndexOf("/"));
		JFile tmpFile = FileSystem.getTempFile(fileName);
		return getFile(file, tmpFile);
	}

	public boolean uploadFile(JFile file, String dbPath) {
		if (!file.exists()) {
			Logger.error("This File doesn't exist!", Level.LVL1);
		}
		if (file.getSize() < CHUNKED_UPLOAD_CHUNK_SIZE * 2)
			return uploadSmallFile(file, dbPath);
		else
			return uploadChunkedFile(file, dbPath);
	}

	public boolean fileExists(String file, String folder) {
		try {
			ListFolderResult result = client.files().listFolder(folder);
			while (result.getHasMore()) {
				result = client.files().listFolderContinue(result.getCursor());
			}
			for (Metadata metadata : result.getEntries()) {
				if (metadata.getName().equals(file))
					return true;
			}
			return false;
		}
		catch (ListFolderErrorException e) {
			Logger.error("Failed to check for existing file. " + e.getMessage(), Level.LVL1);
			return false;
		}
		catch (DbxException e) {
			Logger.error("Failed to check for existing file. " + e.getMessage(), Level.LVL1);
			return false;
		}
	}

	public void deleteFile(String dbFile) {
		try {
			client.files().delete(dbFile);
		}
		catch (DeleteErrorException e) {
			Logger.error("Failed to delete File " + dbFile + " from Dropbox. " + e.getMessage(), Level.LVL1);
		}
		catch (DbxException e) {
			Logger.error("Failed to delete File " + dbFile + " from Dropbox. " + e.getMessage(), Level.LVL1);
		}
	}

	private boolean uploadSmallFile(JFile f, String dbPath) {
		try {
			InputStream stream = f.genFileInputStream();
			FileMetadata metadata = client.files().uploadBuilder(dbPath).withMode(WriteMode.OVERWRITE).withClientModified(f.getLastModified()).uploadAndFinish(stream);

			Logger.info(metadata.toStringMultiline(), Level.LVL3);
		}
		catch (DbxException | IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean uploadChunkedFile(JFile f, String dbPath) {
		long size = f.getSize();
		long uploaded = 0L;
		DbxException thrown = null;

		String sessionId = null;
		for (int i = 0; i < CHUNKED_UPLOAD_MAX_ATTEMPTS; ++i) {
			if (i > 0) {
				Logger.info("Retrying chunked upload (" + (i + 1) + "/" + CHUNKED_UPLOAD_MAX_ATTEMPTS + ")", Level.LVL1);
			}

			try {
				InputStream in = f.genFileInputStream();
				in.skip(uploaded);

				if (sessionId == null) {
					sessionId = client.files().uploadSessionStart().uploadAndFinish(in, CHUNKED_UPLOAD_CHUNK_SIZE).getSessionId();
					uploaded += CHUNKED_UPLOAD_CHUNK_SIZE;
					printProgress(uploaded, size);
				}

				UploadSessionCursor cursor = new UploadSessionCursor(sessionId, uploaded);

				while ((size - uploaded) > CHUNKED_UPLOAD_CHUNK_SIZE) {
					client.files().uploadSessionAppendV2(cursor).uploadAndFinish(in, CHUNKED_UPLOAD_CHUNK_SIZE);
					uploaded += CHUNKED_UPLOAD_CHUNK_SIZE;
					printProgress(uploaded, size);
					cursor = new UploadSessionCursor(sessionId, uploaded);
				}

				long remaining = size - uploaded;
				CommitInfo commitInfo = CommitInfo.newBuilder(dbPath).withMode(WriteMode.ADD).withClientModified(f.getLastModified()).build();
				FileMetadata metadata = client.files().uploadSessionFinish(cursor, commitInfo).uploadAndFinish(in, remaining);

				Logger.info(metadata.toStringMultiline(), Level.LVL1);
				return true;
			}
			catch (RetryException ex) {
				thrown = ex;

				sleepQuietly(ex.getBackoffMillis());
				continue;
			}
			catch (NetworkIOException ex) {
				thrown = ex;
				continue;
			}
			catch (UploadSessionLookupErrorException ex) {
				if (ex.errorValue.isIncorrectOffset()) {
					thrown = ex;

					uploaded = ex.errorValue.getIncorrectOffsetValue().getCorrectOffset();
					continue;
				}
				else {
					Logger.error("Error uploading to Dropbox: " + ex.getMessage(), Level.LVL1);
					return false;
				}
			}
			catch (UploadSessionFinishErrorException ex) {
				if (ex.errorValue.isLookupFailed() && ex.errorValue.getLookupFailedValue().isIncorrectOffset()) {
					thrown = ex;

					uploaded = ex.errorValue.getLookupFailedValue().getIncorrectOffsetValue().getCorrectOffset();
					continue;
				}
				else {

					System.err.println("Error uploading to Dropbox: " + ex.getMessage());
					return false;
				}
			}
			catch (DbxException ex) {
				System.err.println("Error uploading to Dropbox: " + ex.getMessage());
				return false;
			}
			catch (IOException ex) {
				System.err.println("Error reading from file \"" + f + "\": " + ex.getMessage());
				return false;
			}
		}
		System.err.println("Maxed out upload attempts to Dropbox. Most recent error: " + thrown.getClass().getName() + ":" + thrown.getMessage());
		return false;
	}

	private void printProgress(long uploaded, long size) {
		String sizeString;
		if (size > (1024 * 1024 * 1024)) {
			sizeString = ByteUnit.bytesToGigabytes(size) + "GB";
		}
		else
			if (size > (1024 * 1024)) {
				sizeString = ByteUnit.bytesToMegabytes(size) + "MB";
			}
			else
				if (size > 1024)
					sizeString = ByteUnit.bytesToKilobytes(size) + "KB";
				else
					sizeString = size + "B";
		String uploadedString;
		if (uploaded > (1024 * 1024 * 1024))
			uploadedString = ByteUnit.bytesToGigabytes(uploaded) + "GB";
		else
			if (uploaded > (1024 * 1024))
				uploadedString = ByteUnit.bytesToMegabytes(uploaded) + "MB";
			else
				if (uploaded > 1024)
					uploadedString = ByteUnit.bytesToKilobytes(uploaded) + "KB";
				else
					uploadedString = uploaded + "B";
		Logger.info("Uploaded " + uploadedString + "/" + sizeString + " bytes (" + (100 * (uploaded / (double) size)) + "%)", Level.LVL1);
	}

	private void sleepQuietly(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) {
			Logger.error("Failed to sleep Thread: " + e.getMessage(), Level.LVL4);
		}
	}
}
