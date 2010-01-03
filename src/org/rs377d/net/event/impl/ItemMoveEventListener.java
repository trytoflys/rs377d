package org.rs377d.net.event.impl;

import org.apache.mina.core.session.IoSession;
import org.rs377d.model.player.Player;
import org.rs377d.model.util.Container;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.event.EventListener;
import org.rs377d.net.event.EventListenerChain;

public class ItemMoveEventListener implements EventListener
{

	@Override
	public void handle(IoSession session, Rs2Packet packet, EventListenerChain chain) throws Exception
	{
		Player player = (Player) session.getAttribute("player");
		Container inventory = (Container) player.getAttribute("inventory");

		int newSlot = packet.getLEShortA();
		packet.getByteA(); // unknown
		packet.getShortA(); // frame
		int oldSlot = packet.getLEShort();

		inventory.swap(oldSlot, newSlot);

		// sending inventory after move breaks it?
		// player.getActionSender().sendInventory();
	}

}
