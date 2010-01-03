package org.rs377d.net.event.impl;

import org.apache.mina.core.session.IoSession;
import org.rs377d.model.player.Player;
import org.rs377d.model.util.Item;
import org.rs377d.model.util.ItemContainer;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.event.EventListener;
import org.rs377d.net.event.EventListenerChain;

public class EquipEventListener implements EventListener
{

	@Override
	public void handle(IoSession session, Rs2Packet packet, EventListenerChain chain) throws Exception
	{
		Player player = (Player) session.getAttribute("player");
		ItemContainer inventory = (ItemContainer) player.getAttribute("inventory");
		ItemContainer equipment = (ItemContainer) player.getAttribute("equipment");
		
		packet.getLEShort();
		packet.getLEShort();
		int slot = packet.getShortA();
		
		Item item = inventory.get(slot);
		equipment.set(ItemContainer.WEAPON_SLOT, item);
		inventory.remove(item);
		
		player.getActionSender().sendInventory();
		player.getActionSender().sendEquipment();
	}

}
