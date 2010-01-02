package org.rs377d.model.util;

public class ChatMessage
{
	
	private byte[] data;
	private int effects, color;
	
	public ChatMessage(byte[] data, int effects, int color)
	{
		this.data = data;
		this.effects = effects;
		this.color = color;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public int getEffects()
	{
		return effects;
	}
	
	public int getColor()
	{
		return color;
	}

}
