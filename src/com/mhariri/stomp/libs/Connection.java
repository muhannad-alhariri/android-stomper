

package com.mhariri.stomp.libs;

import java.io.UnsupportedEncodingException;
import java.util.Dictionary;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.mhariri.stomp.libs.internal.MessageImpl;
import com.mhariri.stomp.libs.internal.StompSession;



public class Connection {

	public Connection() {
		this("localhost", 61613);
	}

	public Connection(String host, int port) {
		this(host, port, "", "");
	}

	public Connection(String host, int port, String userid, String password) {
		messageHandlers = new ConcurrentHashMap();
		session = new StompSession(host, port, userid, password, this,
				messageHandlers);
	}

	public ErrorMessage connect() throws StompJException {
		return session.connect();
	}

	public void disconnect() {
		session.disconnect();
	}

	public boolean isConnected() {
		return session.isConnected();
	}

	public void subscribe(String destination, boolean autoAck) {
		session.subscribe(destination, autoAck);
	}

	
	public void subscribe(String destination, Dictionary<String, String> headers,boolean autoAck) {
		session.subscribe(destination,headers, autoAck);
	}
	public void unsubscribe(String destination) {
		session.unsubscribe(destination);
	}

	public void send(Message msg, String destination) {
		session.send(msg, destination);
	}

	public void send(String msg, String destination) {
		MessageImpl m = new MessageImpl();
		try {
			m.setContent(msg.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		send(((Message) (m)), destination);
	}

	public void addMessageHandler(String destination, MessageHandler handler) {
		CopyOnWriteArraySet set = new CopyOnWriteArraySet();
		messageHandlers.putIfAbsent(destination, set);
		((CopyOnWriteArraySet) messageHandlers.get(destination)).add(handler);
	}

	public MessageHandler[] getMessageHandlers(String destination) {
		return (MessageHandler[]) ((CopyOnWriteArraySet) messageHandlers
				.get(destination)).toArray(new MessageHandler[0]);
	}

	public void removeMessageHandlers(String destination) {
		messageHandlers.remove("destination");
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(ErrorHandler handler) {
		errorHandler = handler;
	}

	private StompSession session;
	private ConcurrentHashMap messageHandlers;
	private ErrorHandler errorHandler;
}
