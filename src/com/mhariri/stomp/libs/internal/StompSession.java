
package com.mhariri.stomp.libs.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.mhariri.stomp.libs.Connection;
import com.mhariri.stomp.libs.ErrorMessage;
import com.mhariri.stomp.libs.Message;
import com.mhariri.stomp.libs.StompJException;
import com.mhariri.stomp.libs.StompJRuntimeException;


public class StompSession {

	public StompSession(String host, int port, String userid, String password,
			Connection con, Map handlers) {
		this.host = host;
		this.port = port;
		this.userid = userid;
		this.password = password;
		connection = con;
		messageHandlers = handlers;
		autoAckMap = new HashMap();
	}

	public ErrorMessage connect() throws StompJException {
		try {
			socket = new Socket(host, port);
			input = new BufferedInputStream(socket.getInputStream());
			output = new BufferedOutputStream(socket.getOutputStream());
			output.write(createCONNECTFrame(userid, password));
			output.flush();
			frameReceiver = new FrameReceiver(this, input, messageHandlers);
			ErrorMessage errorMsg = frameReceiver.processFirstResponse();
			if (errorMsg == null) {
				frameReceiver.start();
			} else {
				disconnect();
				return errorMsg;
			}
		} catch (UnknownHostException e) {
			disconnect();
			throw new StompJException(e.getMessage(), e);
		} catch (IOException e) {
			disconnect();
			throw new StompJException(e.getMessage(), e);
		}
		return null;
	}

	public void disconnect() {
		sendFrame(createDISCONNECTFrame());
		frameReceiver.interrupt();
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException ioexception) {
		}
	}

	public boolean isConnected() {
		if (socket == null)
			return false;
		else
			return !socket.isClosed() || !socket.isConnected();
	}

	public void subscribe(String destination, boolean autoAck) {
		sendFrame(createSUBSCRIBEFrame(destination, autoAck));
		autoAckMap.put(destination, Boolean.valueOf(autoAck));
	}
	
	public void subscribe(String destination,Dictionary<String, String> headers, boolean autoAck) {
		sendFrame(createSUBSCRIBEFrame(destination,headers, autoAck));
		autoAckMap.put(destination, Boolean.valueOf(autoAck));
	}


	public void unsubscribe(String destination) {
		sendFrame(createUNSUBSCRIBEFrame(destination));
	}

	public String getSessionId() {
		return sessionId;
	}

	private synchronized void sendFrame(byte frame[])
			throws StompJRuntimeException {
		if (!isConnected())
			throw new StompJRuntimeException("Not connected to the server");
		try {
			output.write(frame);
			output.flush();
		} catch (IOException e) {
			
			throw new StompJRuntimeException(e.getMessage(), e);
		}
	}

	void sendAckIfNeeded(Message msg) {
		if (!((Boolean) autoAckMap.get(msg.getDestination())).booleanValue())
			sendFrame(createACKFrame(msg.getMessageId()));
	}

	public void send(Message msg, String destination) {
		sendFrame(createSENDFrame(msg, destination));
	}

	private byte[] createCONNECTFrame(String userid, String password)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(prepareBytes("CONNECT"));
		os.write(10);
		os.write(prepareBytes((new StringBuilder("login: ")).append(userid)
				.toString()));
		os.write(10);
		os.write(prepareBytes((new StringBuilder("passcode:")).append(password)
				.toString()));
		os.write(10);
		os.write(10);
		os.write(0);
		return os.toByteArray();
	}

	private byte[] createSENDFrame(Message msg, String destination) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("SEND"));
			os.write(10);
			os.write(prepareProperty("destination", destination));
			os.write(10);
			String propNames[] = msg.getPropertyNames();
			String as[];
			int j = (as = propNames).length;
			for (int i = 0; i < j; i++) {
				String p = as[i];
				os.write(prepareProperty(p, msg.getProperty(p)));
				os.write(10);
			}

			os.write(10);
			os.write(msg.getContentAsBytes());
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	private byte[] createSUBSCRIBEFrame(String destination, boolean autoAck) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("SUBSCRIBE"));
			os.write(10);
			os.write(prepareProperty("destination", destination));
			os.write(10);
			os.write(prepareProperty("ack", autoAck ? "auto" : "client"));
			os.write(10);
			os.write(10);
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	
	private byte[] createSUBSCRIBEFrame(String destination,Dictionary<String, String> headers ,boolean autoAck) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("SUBSCRIBE"));
			os.write(10);
			os.write(prepareProperty("destination", destination));
			os.write(10);
			for(Enumeration<String> e = headers.keys() ; e.hasMoreElements() ;){
				String headerKey = e.nextElement();
				String headerValue = headers.get(headerKey);
				os.write(prepareProperty(headerKey, headerValue));
				os.write(10);
				Log.i(headerKey, headerValue);
			}
			os.write(prepareProperty("ack", autoAck ? "auto" : "client"));
			os.write(10);
			os.write(10);
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}
	private byte[] createUNSUBSCRIBEFrame(String destination) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("UNSUBSCRIBE"));
			os.write(10);
			os.write(prepareProperty("destination", destination));
			os.write(10);
			os.write(10);
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	private byte[] createACKFrame(String msgId) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("ACK"));
			os.write(10);
			os.write(prepareProperty("message-id", msgId));
			os.write(10);
			os.write(10);
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	private byte[] createDISCONNECTFrame() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(prepareBytes("DISCONNECT"));
			os.write(10);
			os.write(10);
			os.write(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	private byte[] prepareBytes(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] prepareProperty(String propName, String prop) {
		return prepareBytes((new StringBuilder(String.valueOf(propName)))
				.append(":").append(prop).toString());
	}

	Connection getConnection() {
		return connection;
	}

	private String host;
	private int port;
	private String userid;
	private String password;
	private String sessionId;
	private Socket socket;
	private BufferedInputStream input;
	private BufferedOutputStream output;
	private Connection connection;
	private HashMap autoAckMap;
	private FrameReceiver frameReceiver;
	private Map messageHandlers;
}
