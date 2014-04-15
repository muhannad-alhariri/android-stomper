// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ErrorMessage.java

package com.mhariri.stomp.libs;

public interface ErrorMessage {

	public abstract String getMessage();

	public abstract byte[] getContentAsBytes();

	public abstract String getContentAsString();
}
