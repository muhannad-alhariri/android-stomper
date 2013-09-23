
package com.mhariri.stomp.libs.internal;

import java.io.UnsupportedEncodingException;

import com.mhariri.stomp.libs.ErrorMessage;


public class ErrorImpl implements ErrorMessage {

	public ErrorImpl() {
	}

	public byte[] getContentAsBytes() {
		return content;
	}

	public String getContentAsString() {
		try {
			return new String(content, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void setContent(byte content[]) {
		this.content = content;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		message = msg;
	}

	private String message;
	private byte content[];
}
