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
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.Rs2Packet.Type;
import org.rs377d.net.util.ISAACCipher;
import org.rs377d.net.util.Rs2ProtocolUtils;

public class Rs2Decoder extends CumulativeProtocolDecoder
{

	@Override
	protected boolean doDecode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput out) throws Exception
	{
		ISAACCipher decrypter = (ISAACCipher) session.getAttribute("decrypter");
		int opcode = (Integer) session.getAttribute("opcode", -1);
		int size = (Integer) session.getAttribute("size", -1);
		if (opcode == -1)
		{
			if (buffer.remaining() == 0)
				return false;
			opcode = buffer.get() & 0xff;
			opcode = (opcode - decrypter.getNextValue()) & 0xff;
			size = Rs2ProtocolUtils.packetSizes[opcode];
			session.setAttribute("opcode", opcode);
			session.setAttribute("size", size);
		}
		if (size == -1)
		{
			if (buffer.remaining() == 0)
				return false;
			size = buffer.get() & 0xff;
			session.setAttribute("size", size);
		}
		if (buffer.remaining() < size)
			return false;
		byte[] data = new byte[size];
		buffer.get(data);
		IoBuffer payload = IoBuffer.allocate(data.length).put(data).flip();
		out.write(new Rs2Packet(opcode, Type.FIXED, payload));
		session.setAttribute("opcode", -1);
		session.setAttribute("size", -1);
		return true;
	}

}
