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

package org.rs377d.model.player;

import org.apache.mina.core.session.IoSession;
import org.rs377d.model.Entity;
import org.rs377d.model.npc.Npc;
import org.rs377d.model.util.Item;
import org.rs377d.model.util.Container;
import org.rs377d.model.util.EntityList;
import org.rs377d.model.util.Container.Type;
import org.rs377d.net.Rs2Packet;
import org.rs377d.net.util.ActionSender;

public class Player extends Entity
{

	private IoSession session;
	private ActionSender actionSender;
	private String username, password;
	private boolean initialized = false;
	private Rs2Packet cachedUpdateBlock;
	private EntityList<Player> playerList = new EntityList<Player>(256);
	private EntityList<Npc> npcList = new EntityList<Npc>(256);

	public Player(IoSession session, String username, String password)
	{
		this.session = session;
		this.username = username;
		this.password = password;
		actionSender = new ActionSender(this);
		initAttributes();
	}

	private void initAttributes()
	{
		setAttribute("muted", false);
		setAttribute("staffStatus", (byte) 0);
		setAttribute("gender", (byte) 0);
		setAttribute("skullIcon", (byte) -1);
		setAttribute("prayerIcon", (byte) -1);
		setAttribute("hairColor", (byte) 7);
		setAttribute("bodyColor", (byte) 8);
		setAttribute("legsColor", (byte) 9);
		setAttribute("feetColor", (byte) 5);
		setAttribute("skinColor", (byte) 0);
		setAttribute("headModel", 3);
		setAttribute("bodyModel", 20);
		setAttribute("armsModel", 28);
		setAttribute("handsModel", 35);
		setAttribute("legsModel", 39);
		setAttribute("feetModel", 44);
		setAttribute("beardModel", 14);

		Container equipment = new Container(Type.STANDARD, 14);
		setAttribute("equipment", equipment);

		Container inventory = new Container(Type.STANDARD, 28);
		inventory.add(new Item(1163));
		inventory.add(new Item(1127));
		inventory.add(new Item(1079));
		inventory.add(new Item(4131));
		inventory.add(new Item(1201));
		inventory.add(new Item(6585));

		setAttribute("inventory", inventory);

		setAttribute("bank", new Container(Type.ALWAYS_STACK, 496));
	}

	public EntityList<Npc> getNpcList()
	{
		return npcList;
	}

	public EntityList<Player> getPlayerList()
	{
		return playerList;
	}

	public void setCachedUpdateBlock(Rs2Packet cachedUpdateBlock)
	{
		this.cachedUpdateBlock = cachedUpdateBlock;
	}

	public Rs2Packet getCachedUpdateBlock()
	{
		return cachedUpdateBlock;
	}

	public IoSession getSession()
	{
		return session;
	}

	public void setInitialized(boolean initialized)
	{
		this.initialized = initialized;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public ActionSender getActionSender()
	{
		return actionSender;
	}

	public boolean isInitialized()
	{
		return initialized;
	}

}
