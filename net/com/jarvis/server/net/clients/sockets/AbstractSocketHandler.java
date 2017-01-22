package com.jarvis.server.net.clients.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.jarvis.server.JarvisServer;
import com.jarvis.server.concurrency.JTaskBuilder;
import com.jarvis.server.utils.Logger;
import com.jarvis.server.utils.Logger.Level;

public class AbstractSocketHandler {

	private Socket				socket;
	private OutputStream		out;
	private InputStream			in;

	private List<byte[]>		receivedMessages	= new ArrayList<byte[]>();

	private volatile boolean	connected			= false;

	public AbstractSocketHandler(InetAddress address, int port) {
		try {
			socket = new Socket(address, port);
			prepare();
		}
		catch (IOException e) {
			Logger.error("Failed to connect AbstractSocket! " + e.getMessage(), Level.LVL1);
		}
	}

	public AbstractSocketHandler(Socket socket) {
		try {
			this.socket = socket;
			prepare();
		}
		catch (IOException e) {
			Logger.error("Failed to connect AbstractSocket! " + e.getMessage(), Level.LVL1);
		}
	}

	private void prepare() throws IOException {
		in = socket.getInputStream();
		out = socket.getOutputStream();
		connected = true;
		JarvisServer.getNetModule().getThreadPool().submit(JTaskBuilder.newTask().executing(() -> { // TODO change to scheduled task
			while (JarvisServer.getNetModule().isRunning() && connected) {
				receivedMessages.add(receiveInternal());
			}
		}));
	}

	void send(byte[] data) {
		if (!connected) {
			Logger.error("Socket is not connected!", Level.LVL1);
			return;
		}
		try {
			out.write(data);
			out.flush();
		}
		catch (IOException e) {
			Logger.error("Failed to send data to client! " + e.getMessage(), Level.LVL1);
		}
	}

	byte[] receive() {
		while (receivedMessages.size() == 0) {
			try {
				Thread.sleep(250);
			}
			catch (InterruptedException e) {
				Logger.error("Failed to interrupt Thread", Level.LVL2);
			}
		}
		return receivedMessages.remove(0);
	}

	private byte[] receiveInternal() {
		if (!connected) {
			Logger.error("Socket is not connected!", Level.LVL1);
			return null;
		}
		byte[] data = new byte[1024];
		int size = 0;
		while (size == 0) {
			try {
				size = in.read(data);
			}
			catch (IOException e) {
				if (!connected)
					return null;
				Logger.error("Failed to read data from client! " + e.getMessage(), Level.LVL1);
				Logger.error("Disconnecting from client!", Level.LVL1);
				connected = false;
				return null;
			}
		}
		byte[] returnData = new byte[size];
		System.arraycopy(data, 0, returnData, 0, size);
		return returnData;
	}

	public void disconnect() {
		connected = false;
		try {
			socket.close();
		}
		catch (IOException e) {
		}
	}
}
