// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StompJException.java

package com.mhariri.stomp.libs;

public class StompJException extends Exception {

	public StompJException(String message) {
		super(message);
	}

	public StompJException(String message, Throwable cause) {
		super(message, cause);
	}
}
