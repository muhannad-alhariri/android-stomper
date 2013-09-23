
package com.mhariri.stomp.libs;

public interface Message {

	public abstract String getMessageId();

	public abstract String getDestination();

	public abstract String getProperty(String s);

	public abstract String[] getPropertyNames();

	public abstract String getContentAsString();

	public abstract byte[] getContentAsBytes();
}
