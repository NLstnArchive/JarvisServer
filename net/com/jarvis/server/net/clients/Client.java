package com.jarvis.server.net.clients;

import java.net.InetAddress;
import java.net.Socket;

import com.jarvis.server.JarvisServer;
import com.jarvis.server.concurrency.JTaskBuilder;
import com.jarvis.server.net.clients.sockets.FileSocketHandler;
import com.jarvis.server.net.clients.sockets.MessageSocketHandler;
import com.jarvis.server.utils.Logger.Logger;
import com.jarvis.server.utils.Logger.Logger.Level;
import com.jarvis.server.utils.Logger.UUIDGenerator;

public class Client {

	private static UUIDGenerator	uuid				= new UUIDGenerator();

	private volatile boolean		connected			= false;

	private static final String		PREFIX_CONNECTING	= "/c/";

	private MessageSocketHandler	messageHandler;
	private FileSocketHandler		fileHandler;

	private InetAddress				address;

	private final int				id;

	public Client(Socket socket) {
		id = uuid.getUUID();
		address = socket.getInetAddress();
		messageHandler = new MessageSocketHandler(socket);
	}

	public boolean connect() {
		connected = true;
		messageHandler.sendMessage(PREFIX_CONNECTING + id);
		try {
			Thread.sleep(100);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		String connectionMessage = messageHandler.receiveMessage();
		if (!connectionMessage.startsWith(PREFIX_CONNECTING)) {
			Logger.error("Received invalid connection package! " + connectionMessage, Level.LVL1);
			connected = false;
			return connected;
		}
		JarvisServer.getNetModule().getThreadPool().submit(JTaskBuilder.newTask().executing(() -> { // TODO change to scheduled task somehow
			while (JarvisServer.getNetModule().isRunning() && connected) {
				handleMessage(messageHandler.receiveMessage());
			}
		}));
		return connected;
	}

	private void handleMessage(String message) {
		if (message.equals("/dc/"))
			disconnect();
	}

	public void disconnect() {
		Logger.info("Disconnecting client...", Level.LVL1);
		messageHandler.sendMessage("/dc/");
		connected = false;
		uuid.returnID(id);
		messageHandler.disconnect();
		fileHandler.disconnect();
		Logger.info("Successfully disconnected client!", Level.LVL1);
	}

	public void addFileSocket(FileSocketHandler socketHandler) {
		fileHandler = socketHandler;
	}

	public InetAddress getAddress() {
		return address;
	}

	public boolean isConnected() {
		return connected;
	}

	public int getID() {
		return id;
	}

	public boolean hasFileSocket() {
		return fileHandler != null;
	}

}
