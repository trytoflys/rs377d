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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.rs377d.Configuration;
import org.rs377d.ServerExecutor;
import org.rs377d.model.World;
import org.rs377d.model.npc.Npc;
import org.rs377d.model.player.Player;

public class PlayerUpdateCoordinator implements Runnable
{

	private CountDownLatch latch;
	private List<Runnable> preUpdateList = new ArrayList<Runnable>();
	private List<Runnable> playerUpdateList = new ArrayList<Runnable>();
	private List<Runnable> npcUpdateList = new ArrayList<Runnable>();
	private List<Runnable> postUpdateList = new ArrayList<Runnable>();

	@Override
	public void run()
	{
		try
		{
			prepareWork();
			dispatchPreWork();
			dispatchUpdateWork();
			dispatchPostWork();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public CountDownLatch getLatch()
	{
		return latch;
	}

	private void prepareWork()
	{
		preUpdateList.clear();
		playerUpdateList.clear();
		npcUpdateList.clear();
		postUpdateList.clear();

		for (Iterator<Npc> it$ = World.getSingleton().getNpcList().iterator(); it$.hasNext();)
		{
			Npc npc = it$.next();
			preUpdateList.add(new PreNpcUpdate(this, npc));
			postUpdateList.add(new PostNpcUpdate(this, npc));
		}
		for (Iterator<Player> it$ = World.getSingleton().getPlayerList().iterator(); it$.hasNext();)
		{
			Player player = it$.next();
			preUpdateList.add(new PrePlayerUpdate(this, player));
			playerUpdateList.add(new PlayerUpdate(this, player));
			npcUpdateList.add(new NpcUpdate(this, player));
			postUpdateList.add(new PostPlayerUpdate(this, player));
		}
	}

	private void dispatchPreWork() throws InterruptedException
	{
		latch = new CountDownLatch(preUpdateList.size());
		for (Runnable work : preUpdateList)
			ServerExecutor.getThreadPool().submit(work);
		latch.await(Configuration.TICK_RATE, TimeUnit.MILLISECONDS);
	}

	private void dispatchUpdateWork() throws InterruptedException
	{
		latch = new CountDownLatch(playerUpdateList.size());
		for (Runnable work : playerUpdateList)
			ServerExecutor.getThreadPool().submit(work);
		latch.await(Configuration.TICK_RATE, TimeUnit.MILLISECONDS);

		latch = new CountDownLatch(npcUpdateList.size());
		for (Runnable work : npcUpdateList)
			ServerExecutor.getThreadPool().submit(work);
		latch.await(Configuration.TICK_RATE, TimeUnit.MILLISECONDS);
	}

	private void dispatchPostWork() throws InterruptedException
	{
		latch = new CountDownLatch(postUpdateList.size());
		for (Runnable work : postUpdateList)
			ServerExecutor.getThreadPool().submit(work);
		latch.await(Configuration.TICK_RATE, TimeUnit.MILLISECONDS);
	}

}
