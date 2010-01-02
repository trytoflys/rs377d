/*
 * Copyright (C) 2009 Blake Beaupain
 * 
 * This file is part of rs377d.
 * rs377d is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rs377d is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with rs377d.  If not, see <http://www.gnu.org/licenses/>.
 */

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
