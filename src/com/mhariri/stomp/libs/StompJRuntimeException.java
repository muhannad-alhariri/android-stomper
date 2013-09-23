
package com.mhariri.stomp.libs;

public class StompJRuntimeException extends RuntimeException {

	public StompJRuntimeException(String message) {
		super(message);
	}

	public StompJRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
