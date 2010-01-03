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

package org.rs377d.model;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.rs377d.Configuration;
import org.rs377d.ServerExecutor;
import org.rs377d.model.npc.Npc;
import org.rs377d.model.npc.NpcMovementGenerator;
import org.rs377d.model.player.Player;
import org.rs377d.model.util.EntityList;

public class World
{

	private static final World singleton = new World();
	private EntityList<Player> playerList = new EntityList<Player>(Configuration.MAX_PLAYERS);
	private EntityList<Npc> npcList = new EntityList<Npc>(Configuration.MAX_NPCS);

	{
		ServerExecutor.getLogicExecutor().scheduleWithFixedDelay(new NpcMovementGenerator(), 0, 5, TimeUnit.SECONDS);
	}

	public void registerPlayer(Player player)
	{
		System.out.println("\tRegistering player \"" + player.getUsername() + "\" into the world");
		synchronized (playerList)
		{
			playerList.add(player);
		}
	}

	public void registerNpc(Npc npc)
	{
		synchronized (npcList)
		{
			npcList.add(npc);
		}
	}

	public void unregisterPlayer(Player player)
	{
		System.out.println("\tUnregistering player \"" + player.getUsername() + "\" from the world");
		for (Iterator<Player> it$ = playerList.iterator(); it$.hasNext();)
		{
			Player p = it$.next();
			p.getPlayerList().remove(player);
		}
		synchronized (playerList)
		{
			playerList.remove(player);
		}
	}

	public void unregisterNpc(Npc npc)
	{
		for (Iterator<Player> it$ = playerList.iterator(); it$.hasNext();)
		{
			Player p = it$.next();
			synchronized (p.getNpcList())
			{
				p.getNpcList().remove(npc);
			}
		}
		synchronized (npcList)
		{
			npcList.remove(npc);
		}
	}

	public EntityList<Player> getPlayerList()
	{
		return playerList;
	}

	public EntityList<Npc> getNpcList()
	{
		return npcList;
	}

	public static World getSingleton()
	{
		return singleton;
	}

}
