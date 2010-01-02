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
import org.rs377d.net.Rs2Packet.Type;

public class Rs2PacketBuilder
{

	public static final int[] BIT_MASK_OUT = new int[32];
	private int opcode;
	private Type type;
	private IoBuffer payload = IoBuffer.allocate(16);
	private int bitPosition;

	static
	{
		for (int i = 0; i < BIT_MASK_OUT.length; i++)
			BIT_MASK_OUT[i] = (1 << i) - 1;
	}

	public Rs2PacketBuilder()
	{
		this(-1);
	}

	public Rs2PacketBuilder(int opcode)
	{
		this(opcode, Type.FIXED);
	}

	public Rs2PacketBuilder(int opcode, Type type)
	{
		this.opcode = opcode;
		this.type = type;
		payload.setAutoExpand(true);
		payload.setAutoShrink(true);
	}

	public Rs2PacketBuilder put(byte b)
	{
		payload.put(b);
		return this;
	}

	public Rs2PacketBuilder put(byte[] b)
	{
		payload.put(b);
		return this;
	}

	public Rs2PacketBuilder putShort(int s)
	{
		payload.putShort((short) s);
		return this;
	}

	public Rs2PacketBuilder putInt(int i)
	{
		payload.putInt(i);
		return this;
	}

	public Rs2PacketBuilder putLong(long l)
	{
		payload.putLong(l);
		return this;
	}

	public Rs2Packet toPacket()
	{
		return new Rs2Packet(opcode, type, payload.flip().asReadOnlyBuffer());
	}

	public Rs2PacketBuilder putRs2String(String string)
	{
		payload.put(string.getBytes());
		payload.put((byte) '\n');
		return this;
	}

	public Rs2PacketBuilder putShortA(int val)
	{
		payload.put((byte) (val >> 8));
		payload.put((byte) (val + 128));
		return this;
	}

	public Rs2PacketBuilder putByteA(int val)
	{
		payload.put((byte) (val + 128));
		return this;
	}

	public Rs2PacketBuilder putLEShortA(int val)
	{
		payload.put((byte) (val + 128));
		payload.put((byte) (val >> 8));
		return this;
	}

	public boolean isEmpty()
	{
		return payload.position() == 0;
	}

	public Rs2PacketBuilder startBitAccess()
	{
		bitPosition = payload.position() * 8;
		return this;
	}

	public Rs2PacketBuilder finishBitAccess()
	{
		payload.position((bitPosition + 7) / 8);
		return this;
	}

	public Rs2PacketBuilder putBits(int numBits, int value)
	{
		if (!payload.hasArray())
			throw new UnsupportedOperationException("The IoBuffer implementation must support array() for bit usage.");
		int bytes = (int) Math.ceil((double) numBits / 8D) + 1;
		payload.expand((bitPosition + 7) / 8 + bytes);
		byte[] buffer = payload.array();
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;
		for (; numBits > bitOffset; bitOffset = 8)
		{
			buffer[bytePos] &= ~BIT_MASK_OUT[bitOffset];
			buffer[bytePos++] |= (value >> (numBits - bitOffset)) & BIT_MASK_OUT[bitOffset];
			numBits -= bitOffset;
		}
		if (numBits == bitOffset)
		{
			buffer[bytePos] &= ~BIT_MASK_OUT[bitOffset];
			buffer[bytePos] |= value & BIT_MASK_OUT[bitOffset];
		} else
		{
			buffer[bytePos] &= ~(BIT_MASK_OUT[numBits] << (bitOffset - numBits));
			buffer[bytePos] |= (value & BIT_MASK_OUT[numBits]) << (bitOffset - numBits);
		}
		return this;
	}

	public Rs2PacketBuilder put(IoBuffer buf)
	{
		payload.put(buf);
		return this;
	}

	public Rs2PacketBuilder putByteC(int val)
	{
		put((byte) (-val));
		return this;
	}

	public Rs2PacketBuilder putLEShort(int val)
	{
		payload.put((byte) (val));
		payload.put((byte) (val >> 8));
		return this;
	}

	public Rs2PacketBuilder putInt1(int val)
	{
		payload.put((byte) (val >> 8));
		payload.put((byte) val);
		payload.put((byte) (val >> 24));
		payload.put((byte) (val >> 16));
		return this;
	}

	public Rs2PacketBuilder putInt2(int val)
	{
		payload.put((byte) (val >> 16));
		payload.put((byte) (val >> 24));
		payload.put((byte) val);
		payload.put((byte) (val >> 8));
		return this;
	}

	public Rs2PacketBuilder putLEInt(int val)
	{
		payload.put((byte) (val));
		payload.put((byte) (val >> 8));
		payload.put((byte) (val >> 16));
		payload.put((byte) (val >> 24));
		return this;
	}

	public Rs2PacketBuilder put(byte[] data, int offset, int length)
	{
		payload.put(data, offset, length);
		return this;
	}

	public Rs2PacketBuilder putByteA(byte val)
	{
		payload.put((byte) (val + 128));
		return this;
	}

	public Rs2PacketBuilder putByteC(byte val)
	{
		payload.put((byte) (-val));
		return this;
	}

	public Rs2PacketBuilder putByteS(byte val)
	{
		payload.put((byte) (128 - val));
		return this;
	}

	public Rs2PacketBuilder putReverse(byte[] is, int offset, int length)
	{
		for (int i = (offset + length - 1); i >= offset; i--)
			payload.put(is[i]);
		return this;
	}

	public Rs2PacketBuilder putReverseA(byte[] is, int offset, int length)
	{
		for (int i = (offset + length - 1); i >= offset; i--)
			putByteA(is[i]);
		return this;
	}

	public Rs2PacketBuilder putTriByte(int val)
	{
		payload.put((byte) (val >> 16));
		payload.put((byte) (val >> 8));
		payload.put((byte) val);
		return this;
	}

	public Rs2PacketBuilder putSmart(int val)
	{
		if (val >= 128)

			putShort((val + 32768));
		else

			put((byte) val);
		return this;
	}

	public Rs2PacketBuilder putSignedSmart(int val)
	{
		if (val >= 128)
			putShort((val + 49152));
		else
			put((byte) (val + 64));
		return this;
	}

}
