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

package org.rs377d.net;

import org.apache.mina.core.buffer.IoBuffer;
import org.rs377d.net.util.Rs2ProtocolUtils;

public class Rs2Packet
{

	private int opcode;
	private Type type;
	private IoBuffer payload;

	public Rs2Packet(int opcode, Type type, IoBuffer payload)
	{
		this.opcode = opcode;
		this.type = type;
		this.payload = payload;
	}

	public boolean isRaw()
	{
		return opcode == -1;
	}

	public int getOpcode()
	{
		return opcode;
	}

	public Type getType()
	{
		return type;
	}

	public IoBuffer getPayload()
	{
		return payload;
	}

	public int getLength()
	{
		return payload.limit();
	}

	public int get()
	{
		return payload.get();
	}

	public void get(byte[] b)
	{
		payload.get(b);
	}

	public int getShort()
	{
		return payload.getShort();
	}

	public int getInt()
	{
		return payload.getInt();
	}

	public long getLong()
	{
		return payload.getLong();
	}

	public byte getByteC()
	{
		return (byte) (-get());
	}

	public byte getByteS()
	{
		return (byte) (128 - get());
	}

	public short getLEShortA()
	{
		int i = (payload.get() - 128 & 0xff) | ((payload.get() & 0xff) << 8);
		if (i > 32767)
			i -= 0x10000;
		return (short) i;
	}

	public short getLEShort()
	{
		int i = (payload.get() & 0xff) | ((payload.get() & 0xff) << 8);
		if (i > 32767)
			i -= 0x10000;
		return (short) i;
	}

	public int getInt1()
	{
		byte b1 = payload.get();
		byte b2 = payload.get();
		byte b3 = payload.get();
		byte b4 = payload.get();
		return ((b3 << 24) & 0xff) | ((b4 << 16) & 0xff) | ((b1 << 8) & 0xff) | (b2 & 0xff);
	}

	public int getInt2()
	{
		int b1 = payload.get() & 0xff;
		int b2 = payload.get() & 0xff;
		int b3 = payload.get() & 0xff;
		int b4 = payload.get() & 0xff;
		return ((b2 << 24) & 0xff) | ((b1 << 16) & 0xff) | ((b4 << 8) & 0xff) | (b3 & 0xff);
	}

	public int getTriByte()
	{
		return ((payload.get() << 16) & 0xff) | ((payload.get() << 8) & 0xff) | (payload.get() & 0xff);
	}

	public byte getByteA()
	{
		return (byte) (get() - 128);
	}

	public String getRs2String()
	{
		return Rs2ProtocolUtils.getRs2String(payload);
	}

	public short getShortA()
	{
		int i = ((payload.getUnsigned()) << 8) | (payload.get() - 128 & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return (short) i;
	}

	public void getReverse(byte[] is, int offset, int length)
	{
		for (int i = (offset + length - 1); i >= offset; i--)
		{
			is[i] = payload.get();
		}
	}

	public void getReverseA(byte[] is, int offset, int length)
	{
		for (int i = (offset + length - 1); i >= offset; i--)
		{
			is[i] = getByteA();
		}
	}

	public void get(byte[] is, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			is[offset + i] = payload.get();
		}
	}

	public int getSmart()
	{
		int peek = payload.get(payload.position());
		if (peek < 128)
		{
			return (get() & 0xff);
		} else
		{
			return (getShort() & 0xffff) - 32768;
		}
	}

	public enum Type
	{
		FIXED, VARIABLE, VARIABLE_SHORT
	}

}
