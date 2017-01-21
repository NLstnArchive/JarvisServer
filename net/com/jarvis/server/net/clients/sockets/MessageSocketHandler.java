package com.jarvis.server.net.clients.sockets;

import java.net.Socket;

public class MessageSocketHandler extends AbstractSocketHandler {

	public MessageSocketHandler(Socket socket) {
		super(socket);
	}

	public void sendMessage(String message) {
		send(message.getBytes());
	}

	public String receiveMessage() {
		return new String(receive());
	}

}
