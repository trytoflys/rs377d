package org.rs377d.net.update;

import java.util.Iterator;

import org.rs377d.model.World;
import org.rs377d.model.npc.Npc;
import org.rs377d.model.player.Player;
import org.rs377d.net.Rs2PacketBuilder;
import org.rs377d.net.Rs2Packet.Type;

public class NpcUpdate implements Runnable
{

	private PlayerUpdateCoordinator coordinator;
	private Player player;

	public NpcUpdate(PlayerUpdateCoordinator coordinator, Player player)
	{
		this.coordinator = coordinator;
		this.player = player;
	}

	@Override
	public void run()
	{
		try
		{
			Rs2PacketBuilder updateBlock = new Rs2PacketBuilder();
			Rs2PacketBuilder packet = new Rs2PacketBuilder(71, Type.VARIABLE_SHORT);
			packet.startBitAccess();
			packet.putBits(8, player.getNpcList().size());
			synchronized (player.getNpcList())
			{
				for (Iterator<Npc> it$ = player.getNpcList().iterator(); it$.hasNext();)
				{
					Npc npc = it$.next();
					if (World.getSingleton().getNpcList().contains(npc) && !npc.getUpdateFlags().teleported() && npc.getPosition().isWithinDistance(player.getPosition()))
					{
						updateNpcMovement(packet, npc);
						if (npc.getUpdateFlags().update())
							updateNpc(updateBlock, npc);
					} else
					{
						it$.remove();
						packet.putBits(1, 1);
						packet.putBits(2, 3);
					}
				}
			}
			for (Iterator<Npc> it$ = World.getSingleton().getNpcList().iterator(); it$.hasNext();)
			{
				Npc npc = it$.next();
				if (player.getNpcList().size() == 255)
					break;
				if (player.getNpcList().contains(npc))
					continue;
				if (!player.getPosition().isWithinDistance(npc.getPosition()))
					continue;
				synchronized (player.getNpcList())
				{
					player.getNpcList().add(npc);
				}
				player.getActionSender().appendUpdateAdd(npc, packet);
				if (npc.getUpdateFlags().update())
					updateNpc(updateBlock, npc);
			}
			if (!updateBlock.isEmpty())
			{
				packet.putBits(14, 16383);
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

	private void updateNpc(Rs2PacketBuilder packet, Npc npc)
	{
		packet.put((byte) npc.getUpdateFlags().getMask());
		// todo impl masks
	}

	private void updateNpcMovement(Rs2PacketBuilder packet, Npc npc)
	{
		if (npc.getWalkDirection() == -1)
			player.getActionSender().appendUpdateStand(packet, npc);
		else
			// npcs don't run
			player.getActionSender().appendUpdateWalk(packet, npc);
	}

}
