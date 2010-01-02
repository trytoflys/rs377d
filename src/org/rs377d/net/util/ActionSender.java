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

package org.rs377d.net.util;

import org.rs377d.model.npc.Npc;
import org.rs377d.model.player.Player;
import org.rs377d.model.util.ChatMessage;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.Rs2PacketBuilder;
import org.rs377d.net.Rs2Packet.Type;
import org.rs377d.net.update.PlayerUpdate;

public class ActionSender
{

	private final Player player;
	private int[] sidebarInterfaces = new int[]
	{ 2423, 3917, 638, 3213, 1644, 5608, 1151, 65535, 5065, 5715, 2449, 904, 147, 962 };

	public ActionSender(Player player)
	{
		this.player = player;
	}

	public ActionSender sendLogin()
	{
		resetCameraPosition();
		resetButtons();
		for (int i = 0; i < sidebarInterfaces.length; i++)
			setSidebarInterface(i, sidebarInterfaces[i]);
		sendWelcomeScreen();
		return sendMessage("Welcome to RuneScape.");
	}

	public ActionSender sendMessage(String msg)
	{
		player.getSession().write(new Rs2PacketBuilder(63, Type.VARIABLE).putRs2String(msg).toPacket());
		return this;
	}

	public ActionSender resetCameraPosition()
	{
		player.getSession().write(new Rs2PacketBuilder(148).toPacket());
		return this;
	}

	public ActionSender resetButtons()
	{
		player.getSession().write(new Rs2PacketBuilder(113).toPacket());
		return this;
	}

	public ActionSender setSidebarInterface(int barId, int interfaceId)
	{
		player.getSession().write(new Rs2PacketBuilder(10).putByteS((byte) barId).putShortA(interfaceId).toPacket());
		return this;
	}

	public ActionSender sendMapRegion()
	{
		player.getSession().write(new Rs2PacketBuilder(222).putShort(player.getPosition().getRegionY() + 6).putLEShortA(player.getPosition().getRegionX() + 6).toPacket());
		return this;
	}

	public ActionSender sendWelcomeScreen()
	{
		Rs2PacketBuilder packet = new Rs2PacketBuilder(76);
		packet.putLEShort(0);
		packet.putLEShortA(0);
		packet.putShort(0);
		packet.putShort(0);
		packet.putLEShort(1337);
		packet.putShortA(2);
		packet.putShortA(0);
		packet.putShort(0);
		packet.putLEInt(0);
		packet.putLEShort(0);
		packet.putByteS((byte) 0);
		sendFullScreenInterface(15244, 5993);
		player.getSession().write(packet.toPacket());
		return this;
	}

	public ActionSender sendFullScreenInterface(int interfaceID, int bgInterfaceID)
	{
		player.getSession().write(new Rs2PacketBuilder(253).putLEShort(bgInterfaceID).putShortA(interfaceID).toPacket());
		return this;
	}

	public ActionSender appendUpdateChat(Rs2PacketBuilder packet)
	{
		ChatMessage msg = (ChatMessage) player.getAttribute("chatMessage");
		packet.putShort(((msg.getColor() & 0xff) << 8) + (msg.getEffects() & 0xff));
		packet.putByteC((Byte) player.getAttribute("staffStatus"));
		packet.putByteA((byte) msg.getData().length);
		for (int i = 0; i < msg.getData().length; i++)
			packet.putByteA(msg.getData()[i]);
		return this;
	}

	public ActionSender appendUpdateAppearance(Rs2PacketBuilder packet)
	{
		Rs2PacketBuilder appearance = new Rs2PacketBuilder();
		appearance.put((Byte) player.getAttribute("gender"));
		appearance.put((Byte) player.getAttribute("skullIcon"));
		appearance.put((Byte) player.getAttribute("prayerIcon"));
		appearance.put((byte) 0); // hat
		appearance.put((byte) 0); // cape
		appearance.put((byte) 0); // amulet
		appearance.put((byte) 0); // weapon
		appearance.putShort(0x100 + (Integer) player.getAttribute("bodyModel")); // body
		appearance.put((byte) 0); // shield
		appearance.putShort(0x100 + (Integer) player.getAttribute("armsModel")); // arms
		appearance.putShort(0x100 + (Integer) player.getAttribute("legsModel")); // legs
		appearance.putShort(0x100 + (Integer) player.getAttribute("headModel")); // head
		appearance.putShort(0x100 + (Integer) player.getAttribute("handsModel")); // hands
		appearance.putShort(0x100 + (Integer) player.getAttribute("feetModel")); // feet
		appearance.put((byte) 0);
		appearance.put((Byte) player.getAttribute("hairColor"));
		appearance.put((Byte) player.getAttribute("bodyColor"));
		appearance.put((Byte) player.getAttribute("legsColor"));
		appearance.put((Byte) player.getAttribute("feetColor"));
		appearance.put((Byte) player.getAttribute("skinColor"));

		for (int i : PlayerUpdate.ANIMATION_INDICES)
			appearance.putShort(i);

		appearance.putLong(Rs2ProtocolUtils.playerNameToLong(player.getUsername()));
		appearance.put((byte) 3);
		appearance.putShort(0);

		Rs2Packet p = appearance.toPacket();

		byte[] buffer = new byte[p.getLength()];
		p.getReverse(buffer, 0, p.getLength());

		packet.put((byte) p.getLength());
		packet.put(buffer);
		return this;
	}

	public ActionSender appendUpdateAdd(Npc npc, Rs2PacketBuilder packet)
	{
		int yPos = npc.getPosition().getY() - player.getPosition().getY();
		int xPos = npc.getPosition().getX() - player.getPosition().getX();
		System.out.println("adding npc, dist from plr: " + xPos + ", " + yPos);
		packet.putBits(14, npc.getIndex());
		packet.putBits(1, npc.getUpdateFlags().update() ? 1 : 0);
		packet.putBits(5, yPos);
		packet.putBits(5, xPos);
		packet.putBits(1, 0);
		packet.putBits(13, npc.getID());
		return this;
	}

	public ActionSender appendUpdateAdd(Player player, Rs2PacketBuilder packet)
	{
		int yPos = player.getPosition().getY() - this.player.getPosition().getY();
		int xPos = player.getPosition().getX() - this.player.getPosition().getX();
		packet.putBits(11, player.getIndex() + 32768);
		packet.putBits(5, xPos);
		packet.putBits(1, 1);
		packet.putBits(1, 1);
		packet.putBits(5, yPos);
		return this;
	}

	public ActionSender appendUpdateStand(Rs2PacketBuilder packet, boolean updateRequired)
	{
		if (updateRequired)
		{
			packet.putBits(1, 1);
			packet.putBits(2, 0);
		} else
			packet.putBits(1, 0);
		return this;
	}

	public ActionSender appendUpdateStand(Rs2PacketBuilder packet, Npc npc)
	{
		if (npc.getUpdateFlags().update())
		{
			packet.putBits(1, 1);
			packet.putBits(2, 0);
		} else
			packet.putBits(1, 0);
		return this;
	}

	public ActionSender appendUpdateWalk(Rs2PacketBuilder packet, Npc npc)
	{
		System.out.println("npc walk");
		packet.putBits(1, 1);
		packet.putBits(2, 1);
		packet.putBits(3, npc.getWalkDirection());
		packet.putBits(1, npc.getUpdateFlags().update() ? 1 : 0);
		return this;
	}

	public ActionSender appendUpdateWalk(Rs2PacketBuilder packet, boolean updateRequired)
	{
		packet.putBits(1, 1);
		packet.putBits(2, 1);
		packet.putBits(3, player.getWalkDirection());
		packet.putBits(1, updateRequired ? 1 : 0);
		return this;
	}

	public ActionSender appendUpdateRun(Rs2PacketBuilder packet, boolean updateRequired)
	{
		packet.putBits(1, 1);
		packet.putBits(2, 2);
		packet.putBits(3, player.getWalkDirection());
		packet.putBits(3, player.getRunDirection());
		packet.putBits(1, updateRequired ? 1 : 0);
		return this;
	}

	public ActionSender appendUpdateTeleport(Rs2PacketBuilder packet, boolean updateRequired)
	{
		packet.putBits(1, 1);
		packet.putBits(2, 3);
		packet.putBits(1, 0);
		packet.putBits(2, player.getPosition().getZ());
		packet.putBits(7, player.getPosition().getLocalY());
		packet.putBits(7, player.getPosition().getLocalX());
		packet.putBits(1, updateRequired ? 1 : 0);
		return this;
	}

}
