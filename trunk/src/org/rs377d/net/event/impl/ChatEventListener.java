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

package org.rs377d.net.event.impl;

import org.apache.mina.core.session.IoSession;
import org.rs377d.model.player.Player;
import org.rs377d.model.util.ChatMessage;
import org.rs377d.model.util.UpdateFlags.Flag;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.event.EventListenerChain;
import org.rs377d.net.event.EventListener;

public class ChatEventListener implements EventListener
{

	@Override
	public void handle(IoSession session, Rs2Packet packet, EventListenerChain chain) throws Exception
	{
		Player player = (Player) session.getAttribute("player");
		int color = packet.getByteC();
		int effects = packet.getByteA();
		int length = packet.getLength() - 2;
		byte[] chatData = new byte[length];
		packet.get(chatData);
		player.getUpdateFlags().flag(Flag.CHAT);
		player.setAttribute("chatMessage", new ChatMessage(chatData, effects, color));
	}

}
