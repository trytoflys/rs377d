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

import java.security.SecureRandom;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.rs377d.model.World;
import org.rs377d.model.player.Player;
import org.rs377d.net.Rs2PacketBuilder;
import org.rs377d.net.util.ISAACCipher;
import org.rs377d.net.util.Rs2ProtocolUtils;
import org.rs377d.net.util.RsaDecoder;

public class Rs2LoginDecoder extends CumulativeProtocolDecoder
{

	private SecureRandom random = new SecureRandom();

	private static final byte[] INITIAL_RESPONSE = new byte[]
	{ 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };

	@Override
	protected boolean doDecode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput out) throws Exception
	{
		LoginStage stage = (LoginStage) session.getAttribute("loginStage", LoginStage.REQUEST);
		switch (stage)
		{
		case REQUEST:
			if (buffer.remaining() < 2)
			{
				buffer.rewind();
				return false;
			}
			int loginOpcode = buffer.get() & 0xff;
			if (loginOpcode != 0xe)
			{
				session.close(false);
				return false;
			}
			buffer.position(buffer.position() + 1);
			int responseCode = 0x0;
			long key = random.nextLong();
			session.setAttribute("serverKey", key);
			session.write(new Rs2PacketBuilder().put(INITIAL_RESPONSE).put((byte) responseCode).putLong(key).toPacket());

			session.setAttribute("loginStage", LoginStage.INFO_BLOCK);
			return true;
		case INFO_BLOCK:
			if (buffer.remaining() < 7)
			{
				buffer.rewind();
				return false;
			}
			int sessionStatus = buffer.get() & 0xff;
			if (sessionStatus != 16 && sessionStatus != 18)
			{
				session.close(false);
				return false;
			}
			int encryptedBlockSize = (buffer.get() & 0xff) - 40;
			int magicId = buffer.get() & 0xff;
			if (magicId != 0xff)
			{
				session.close(false);
				return false;
			}
			int clientRevision = buffer.getShort() & 0xffff;
			if (clientRevision != 377)
			{
				session.close(false);
				return false;
			}
			buffer.position(buffer.position() + 37);
			encryptedBlockSize--;
			int reportedSize = buffer.get() & 0xff;
			if (reportedSize != encryptedBlockSize)
			{
				buffer.rewind();
				return false;
			}

			/*
			 * decode the RSA block, because we have our own key pair! we're
			 * doing it the ghetto way for now because the put(src, offset, len)
			 * and wrap(src, offset, len) methods don't seem to be working
			 * properly...
			 */
			IoBuffer decodedBuffer = IoBuffer.allocate(encryptedBlockSize);
			byte[] encryptedPayload = new byte[encryptedBlockSize];
			for (int i = 0; i < encryptedBlockSize; i++)
				encryptedPayload[i] = buffer.get();
			decodedBuffer.put(RsaDecoder.decode(encryptedPayload)).flip();
			int rsaOpcode = decodedBuffer.get();
			if (rsaOpcode != 10)
			{
				// decoding failed
				session.close(false);
				return false;
			}

			long clientKey = decodedBuffer.getLong();
			long serverKey = (Long) session.getAttribute("serverKey");
			if (serverKey != decodedBuffer.getLong())
			{
				session.close(false);
				return false;
			}
			decodedBuffer.position(decodedBuffer.position() + 4);
			String username = Rs2ProtocolUtils.getRs2String(decodedBuffer);
			String password = Rs2ProtocolUtils.getRs2String(decodedBuffer);
			// todo check player auth
			Player player = new Player(session, username, password);
			session.setAttribute("player", player);
			World.getSingleton().registerPlayer(player);
			int[] isaacSeed = new int[]
			{ (int) (clientKey >> 0x20), (int) clientKey, (int) (serverKey >> 0x20), (int) serverKey };
			session.setAttribute("decrypter", new ISAACCipher(isaacSeed));
			for (int i = 0; i < isaacSeed.length; i++)
				isaacSeed[i] += 0x32;
			session.setAttribute("encrypter", new ISAACCipher(isaacSeed));
			session.removeAttribute("loginStage");
			session.removeAttribute("serverKey");
			session.write(new Rs2PacketBuilder().put((byte) 2).put((Byte) player.getAttribute("staffStatus")).put((byte) 0).toPacket());
			session.getFilterChain().remove("protocol");
			session.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(Rs2CodecFactory.GAME));
			player.getActionSender().sendLogin();
			return true;
		}
		return false;
	}

	private enum LoginStage
	{
		REQUEST, INFO_BLOCK
	}

}
