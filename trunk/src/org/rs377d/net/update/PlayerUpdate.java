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

package org.rs377d.net.update;

import java.util.Iterator;

import org.rs377d.model.World;
import org.rs377d.model.player.Player;
import org.rs377d.model.util.UpdateFlags.Flag;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.Rs2PacketBuilder;
import org.rs377d.net.Rs2Packet.Type;

public class PlayerUpdate implements Runnable
{

	private final PlayerUpdateCoordinator coordinator;
	private final Player player;

	public PlayerUpdate(PlayerUpdateCoordinator coordinator, Player player)
	{
		this.coordinator = coordinator;
		this.player = player;
	}

	@Override
	public void run()
	{
		Thread.currentThread().setName(getClass().getSimpleName());
		try
		{
			if (!player.isInitialized())
			{
				player.getUpdateFlags().flag(Flag.APPEARANCE);
				player.getUpdateFlags().flag(Flag.MAP_REGION_CHANGED);
				player.getUpdateFlags().flag(Flag.TELEPORTED);
				player.setInitialized(true);
			}
			if (player.getUpdateFlags().mapRegionChanged())
				player.getActionSender().sendMapRegion();
			Rs2PacketBuilder updateBlock = new Rs2PacketBuilder();
			Rs2PacketBuilder packet = new Rs2PacketBuilder(90, Type.VARIABLE_SHORT);
			packet.startBitAccess();
			updateThisPlayerMovement(packet);
			updatePlayer(updateBlock, player, false, true);
			packet.putBits(8, player.getPlayerList().size());
			for (Iterator<Player> it$ = player.getPlayerList().iterator(); it$.hasNext();)
			{
				Player player = it$.next();
				if (this.player.getPosition().isWithinDistance(player.getPosition()))
				{
					updatePlayerMovement(packet, player);
					if (player.getUpdateFlags().update())
						updatePlayer(updateBlock, player, false, false);
				} else
				{
					this.player.getPlayerList().remove(player);
					packet.putBits(1, 1);
					packet.putBits(2, 3);
				}
			}
			for (Iterator<Player> it$ = World.getSingleton().getPlayerList().iterator(); it$.hasNext();)
			{
				Player player = it$.next();
				if (this.player.getPlayerList().size() == 255)
					break;
				if (player == this.player || this.player.getPlayerList().contains(player))
					continue;
				this.player.getPlayerList().add(player);
				this.player.getActionSender().appendUpdateAdd(player, packet);
				updatePlayer(updateBlock, player, true, false);
			}
			if (!updateBlock.isEmpty())
			{
				packet.putBits(11, 2047);
				packet.finishBitAccess();
				packet.put(updateBlock.toPacket().getPayload());
			} else
				packet.finishBitAccess();
			synchronized (player)
			{
				player.getSession().write(packet.toPacket());
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		} finally
		{
			coordinator.getLatch().countDown();
		}
	}

	private void updatePlayer(Rs2PacketBuilder packet, Player player, boolean forceAppearance, boolean noChat)
	{
		if (forceAppearance)
			player.getUpdateFlags().flag(Flag.APPEARANCE);

		if (!player.getUpdateFlags().update() && !forceAppearance)
			return;

		synchronized (player)
		{
			if (player.getCachedUpdateBlock() != null && player != this.player)
			{
				packet.put(player.getCachedUpdateBlock().getPayload().flip());
				return;
			}
			Rs2PacketBuilder block = new Rs2PacketBuilder();
			int mask = player.getUpdateFlags().getMask();

			if (player.getUpdateFlags().chat() && noChat)
				mask &= 0x8;

			if (mask > 0xff)
			{
				mask |= 0x20;
				block.put((byte) (mask & 0xff));
				block.put((byte) (mask >> 8));
			} else
				block.put((byte) mask);

			if (player.getUpdateFlags().chat() && !noChat)
				player.getActionSender().appendUpdateChat(block);

			if (player.getUpdateFlags().appearance())
				player.getActionSender().appendUpdateAppearance(block);

			Rs2Packet blockPacket = block.toPacket();
			if (player != this.player)
				player.setCachedUpdateBlock(blockPacket);
			packet.put(blockPacket.getPayload());
		}
	}

	private void updateThisPlayerMovement(Rs2PacketBuilder packet)
	{
		if (player.getUpdateFlags().teleported() || player.getUpdateFlags().mapRegionChanged())
			player.getActionSender().appendUpdateTeleport(packet, player.getUpdateFlags().update());
		else if (player.getWalkDirection() == -1)
			player.getActionSender().appendUpdateStand(packet, player.getUpdateFlags().update());
		else
		{
			if (player.getRunDirection() == -1)
				player.getActionSender().appendUpdateWalk(packet, player.getUpdateFlags().update());
			else
				player.getActionSender().appendUpdateRun(packet, player.getUpdateFlags().update());
		}
	}

	private void updatePlayerMovement(Rs2PacketBuilder packet, Player player)
	{
		if (player.getWalkDirection() == -1)
			player.getActionSender().appendUpdateStand(packet, player.getUpdateFlags().update());
		else
		{
			if (player.getRunDirection() == -1)
				player.getActionSender().appendUpdateWalk(packet, player.getUpdateFlags().update());
			else
				player.getActionSender().appendUpdateRun(packet, player.getUpdateFlags().update());
		}
	}

	public static final int[] ANIMATION_INDICES =
	{ 0x328, 0x337, 0x333, 0x334, 0x335, 0x336, 0x338 };

}
