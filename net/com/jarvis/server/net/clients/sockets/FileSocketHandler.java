package com.jarvis.server.net.clients.sockets;

import java.net.Socket;

import com.jarvis.server.data.files.JFile;

public class FileSocketHandler extends AbstractSocketHandler {

	public FileSocketHandler(Socket socket) {
		super(socket);
	}

	public void sendFile(JFile f) {
		send(f.toStream());
	}

	public JFile receiveFile() {
		return JFile.fromStream(receive());
	}

}
