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

package org.rs377d.net.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.Rs2Packet.Type;
import org.rs377d.net.util.ISAACCipher;

public class Rs2Encoder implements ProtocolEncoder
{

	@Override
	public void encode(IoSession session, Object in, ProtocolEncoderOutput out) throws Exception
	{
		Rs2Packet packet = (Rs2Packet) in;
		if (packet.isRaw())
			out.write(packet.getPayload());
		else
		{
			ISAACCipher encrypter = (ISAACCipher) session.getAttribute("encrypter");
			int opcode = packet.getOpcode();
			Type type = packet.getType();
			int size = packet.getLength();
			opcode += encrypter.getNextValue();
			int length = size + 1;
			switch (type)
			{
			case VARIABLE:
				length++;
				break;
			case VARIABLE_SHORT:
				length += 2;
				break;
			}
			IoBuffer buffer = IoBuffer.allocate(length);
			buffer.put((byte) opcode);
			switch (type)
			{
			case VARIABLE:
				buffer.put((byte) size);
				break;
			case VARIABLE_SHORT:
				buffer.putShort((short) size);
				break;
			}
			buffer.put(packet.getPayload());
			out.write(buffer.flip());
		}
	}

	@Override
	public void dispose(IoSession session) throws Exception
	{
	}

}
