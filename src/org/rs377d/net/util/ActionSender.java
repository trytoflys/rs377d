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
import org.rs377d.model.util.Item;
import org.rs377d.model.util.ItemContainer;
import org.rs377d.model.util.UpdateFlags.Flag;
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
		sendInventory();
		sendEquipment();
		return sendMessage("Welcome to RuneScape.");
	}

	public ActionSender sendMessage(String msg)
	{
		player.getSession().write(new Rs2PacketBuilder(63, Type.VARIABLE).putRs2String(msg).toPacket());
		return this;
	}

	public ActionSender sendInventory()
	{
		ItemContainer inventory = (ItemContainer) player.getAttribute("inventory");
		Rs2PacketBuilder packet = new Rs2PacketBuilder(206, Type.VARIABLE_SHORT);
		packet.putShort(3214);
		packet.putShort(inventory.getSize());
		for (Item item : inventory.getItems())
		{
			if (item == null)
			{
				packet.putLEShortA(0);
				packet.putByteC(0);
			} else
			{
				packet.putLEShortA(item.getID() + 1);
				if (item.getAmount() >= 0xff)
				{
					packet.putByteC(255);
					packet.putLEInt(item.getAmount());
				} else
					packet.putByteC(item.getAmount());
			}
		}
		player.getSession().write(packet.toPacket());
		return this;
	}

	public ActionSender sendEquipment()
	{
		player.getUpdateFlags().flag(Flag.APPEARANCE);
		ItemContainer equipment = (ItemContainer) player.getAttribute("equipment");
		Rs2PacketBuilder packet = new Rs2PacketBuilder(134, Type.VARIABLE_SHORT);
		packet.putShort(1688);
		for (int i = 0; i < equipment.getItems().length; i++)
		{
			Item item = equipment.getItems()[i];
			if (item == null)
			{
				packet.put((byte) i);
				packet.putShort(0);
				packet.put((byte) 0);
			} else
			{
				packet.put((byte) i);
				packet.putShort(item.getID() + 1);
				if (item.getAmount() >= 0xff)
				{
					packet.put((byte) 255);
					packet.putInt(item.getAmount());
				} else
					packet.put((byte) item.getAmount());
			}
		}
		player.getSession().write(packet.toPacket());
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
		ItemContainer equipment = (ItemContainer) player.getAttribute("equipment");
		Rs2PacketBuilder appearance = new Rs2PacketBuilder();
		appearance.put((Byte) player.getAttribute("gender"));
		appearance.put((Byte) player.getAttribute("skullIcon"));
		appearance.put((Byte) player.getAttribute("prayerIcon"));

		Item hat = equipment.get(ItemContainer.HEAD_SLOT);
		if (hat != null)
			appearance.putShort(0x200 + hat.getID());
		else
			appearance.put((byte) 0);

		Item cape = equipment.get(ItemContainer.CAPE_SLOT);
		if (cape != null)
			appearance.putShort(0x200 + cape.getID());
		else
			appearance.put((byte) 0);

		Item amulet = equipment.get(ItemContainer.AMULET_SLOT);
		if (amulet != null)
			appearance.putShort(0x200 + amulet.getID());
		else
			appearance.put((byte) 0);

		Item weapon = equipment.get(ItemContainer.WEAPON_SLOT);
		if (weapon != null)
			appearance.putShort(0x200 + weapon.getID());
		else
			appearance.put((byte) 0);

		Item body = equipment.get(ItemContainer.BODY_SLOT);
		if (body != null)
			appearance.putShort(0x200 + body.getID());
		else
			appearance.putShort(0x100 + (Integer) player.getAttribute("bodyModel"));

		Item shield = equipment.get(ItemContainer.SHIELD_SLOT);
		if (shield != null)
			appearance.putShort(0x200 + shield.getID());
		else
			appearance.put((byte) 0);

		if (body == null)
			appearance.putShort(0x100 + (Integer) player.getAttribute("armsModel"));
		else
			appearance.put((byte) 0);

		Item legs = equipment.get(ItemContainer.LEGS_SLOT);
		if (legs != null)
			appearance.putShort(0x200 + legs.getID());
		else
			appearance.putShort(0x100 + (Integer) player.getAttribute("legsModel"));

		// todo full helm/mask check - head atm is invisible with simple hat
		if (hat == null)
			appearance.putShort(0x100 + (Integer) player.getAttribute("headModel"));
		else
			appearance.put((byte) 0);

		Item hands = equipment.get(ItemContainer.HANDS_SLOT);
		if (hands != null)
			appearance.putShort(0x200 + hands.getID());
		else
			appearance.putShort(0x100 + (Integer) player.getAttribute("handsModel"));

		Item feet = equipment.get(ItemContainer.FEET_SLOT);
		if (feet != null)
			appearance.putShort(0x200 + feet.getID());
		else
			appearance.putShort(0x100 + (Integer) player.getAttribute("feetModel"));

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
