package org.rs377d.net.event.impl;

import org.apache.mina.core.session.IoSession;
import org.rs377d.model.player.Player;
import org.rs377d.model.util.Item;
import org.rs377d.model.util.ItemContainer;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.event.EventListener;
import org.rs377d.net.event.EventListenerChain;

public class EquipEventFilter implements EventListener
{

	@Override
	public void handle(IoSession session, Rs2Packet packet, EventListenerChain chain) throws Exception
	{
		Player player = (Player) session.getAttribute("player");
		ItemContainer inventory = (ItemContainer) player.getAttribute("inventory");
		
		packet.getLEShort();
		int itemID = packet.getLEShort();
		int slot = packet.getShortA();
		
		Item item = inventory.get(slot);
		if (item == null)
			return;
		if (item.getID() != itemID)
			return;
		
		packet.getPayload().rewind();
		chain.getNext().handle(session, packet, chain);
	}

}
