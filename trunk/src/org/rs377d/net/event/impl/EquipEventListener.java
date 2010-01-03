package org.rs377d.net.event.impl;

import org.apache.mina.core.session.IoSession;
import org.rs377d.model.player.Player;
import org.rs377d.model.util.Container;
import org.rs377d.model.util.Equipment;
import org.rs377d.model.util.Item;
import org.rs377d.model.util.Equipment.EquipmentType;
import org.rs377d.model.util.UpdateFlags.Flag;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.event.EventListener;
import org.rs377d.net.event.EventListenerChain;

public class EquipEventListener implements EventListener
{

	@Override
	public void handle(IoSession session, Rs2Packet packet, EventListenerChain chain) throws Exception
	{
		Player player = (Player) session.getAttribute("player");
		Container inventory = (Container) player.getAttribute("inventory");
		Container equipment = (Container) player.getAttribute("equipment");

		packet.getLEShort();
		int itemID = packet.getLEShort();
		int slot = packet.getShortA();

		Item item = inventory.get(slot);
		if (item != null && item.getId() == itemID)
		{
			EquipmentType type = Equipment.getType(item);
			Item oldEquip = null;
			boolean stackable = item.getDefinition().isStackable();
			if (equipment.isSlotUsed(type.getSlot()) && !stackable)
			{
				oldEquip = equipment.get(type.getSlot());
				equipment.set(type.getSlot(), null);
			}
			inventory.set(slot, null);
			if (oldEquip != null)
				inventory.add(oldEquip);
			if (!stackable)
				equipment.set(type.getSlot(), item);
			else
				equipment.add(item);
		}

		player.getActionSender().sendEquipment();
		player.getActionSender().sendInventory();
		player.getUpdateFlags().flag(Flag.APPEARANCE);
	}

}
