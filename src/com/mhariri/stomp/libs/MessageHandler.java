// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MessageHandler.java

package com.mhariri.stomp.libs;

// Referenced classes of package pk.aamir.stompj:
//            Message

public interface MessageHandler {

	public abstract void onMessage(Message message);
}
