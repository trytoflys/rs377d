package org.rs377d.net.event.impl;

import org.apache.mina.core.session.IoSession;
import org.rs377d.model.player.Player;
import org.rs377d.model.util.Container;
import org.rs377d.model.util.UpdateFlags.Flag;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.event.EventListener;
import org.rs377d.net.event.EventListenerChain;

public class UnequipEventListener implements EventListener
{

	@Override
	public void handle(IoSession session, Rs2Packet packet, EventListenerChain chain) throws Exception
	{
		Player player = (Player) session.getAttribute("player");
		Container equipment = (Container) player.getAttribute("equipment");
		Container inventory = (Container) player.getAttribute("inventory");

		int itemID = packet.getShortA();
		packet.getShort();
		int slot = packet.getShort();

		Container.transfer(equipment, inventory, slot, itemID);

		player.getActionSender().sendInventory();
		player.getActionSender().sendEquipment();
		player.getUpdateFlags().flag(Flag.APPEARANCE);
	}

}
