package com.jarvis.server.net.clients;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.jarvis.server.JarvisServer;
import com.jarvis.server.concurrency.JTaskBuilder;
import com.jarvis.server.net.clients.sockets.FileSocketHandler;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class ClientHandler {

	private List<Client>	clients				= new ArrayList<Client>();

	private List<Socket>	pendingFileSockets	= new ArrayList<Socket>();

	private ServerSocket	messageSocket;
	private ServerSocket	fileSocket;

	public ClientHandler() {
		try {
			messageSocket = new ServerSocket(61243);
			fileSocket = new ServerSocket(49506);
		}
		catch (IOException e) {
			Logger.error("Failed to create Socket on port 61243", Level.LVL1);
			e.printStackTrace();
		}
		Runnable acceptMessageConnection = new Runnable() {
			public void run() {
				while (JarvisServer.getNetModule().isRunning()) {
					try {
						Socket socket = messageSocket.accept();
						addClient(socket);
					}
					catch (IOException e) {
						Logger.error("Failed to accept new client! " + e.getMessage(), Level.LVL1);
					}
				}
			}
		};
		Runnable acceptFileConnection = new Runnable() {
			public void run() {
				while (JarvisServer.getNetModule().isRunning()) {
					try {
						Socket socket = fileSocket.accept();
						pendingFileSockets.add(socket);
					}
					catch (IOException e) {
						Logger.error("Failed to accept new client! " + e.getMessage(), Level.LVL1);
					}
				}
			}
		};
		JarvisServer.getNetModule().getThreadPool().submit(JTaskBuilder.newTask().executing(acceptMessageConnection));
		JarvisServer.getNetModule().getThreadPool().submit(JTaskBuilder.newTask().executing(acceptFileConnection));
	}

	private void addClient(Socket socket) {
		Client client = new Client(socket);
		Logger.info("Trying to connect new client from " + client.getAddress(), Level.LVL1);
		if (client.connect()) {
			clients.add(client);
			for (Socket fileSocket : pendingFileSockets)
				handleFileSocket(fileSocket);
			if (client.hasFileSocket())
				Logger.info("New Client connected!", Level.LVL1);
			else
				Logger.error("Failed to connect FileSocket for client " + client.getAddress() + "!", Level.LVL1);
		}
	}

	private void handleFileSocket(Socket socket) {
		Logger.info("New FileSocket connecting...", Level.LVL1);
		byte[] message = new byte[1024];
		int size = 0;
		try {
			while (size == 0) {
				size = socket.getInputStream().read(message);
			}
			String connectionMessage = new String(message, 0, size);
			int id = Integer.parseInt(connectionMessage.split("/")[2]);
			for (Client client : clients) {
				if (client.getID() == id) {
					client.addFileSocket(new FileSocketHandler(socket));
					socket.getOutputStream().write("/c/".getBytes());
					socket.getOutputStream().flush();
					Logger.info("Successfully matched FileSocket!", Level.LVL1);
					return;
				}
			}
		}
		catch (Exception e) {
			Logger.error("Error while connecting FileSocket!", Level.LVL1);
		}
		Logger.error("Failed to match FileSocket to client!", Level.LVL1);
	}
}
