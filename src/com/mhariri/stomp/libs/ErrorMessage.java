
package com.mhariri.stomp.libs;

public interface ErrorMessage {

	public abstract String getMessage();

	public abstract byte[] getContentAsBytes();

	public abstract String getContentAsString();
}
