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

package org.rs377d.model.util;

import java.util.Deque;
import java.util.LinkedList;

import org.rs377d.model.Entity;
import org.rs377d.model.util.UpdateFlags.Flag;
import org.rs377d.util.DirectionFinder;

public class WalkingManager
{

	private Entity entity;
	private Deque<Point> waypoints = new LinkedList<Point>();
	private boolean isRunning = false, runQueue = false;

	public WalkingManager(Entity entity)
	{
		this.entity = entity;
	}

	public void reset()
	{
		runQueue = false;
		waypoints.clear();
		waypoints.add(new Point(entity.getPosition().getX(), entity.getPosition().getY(), -1));
	}

	public void processNextMovement()
	{
		Position posBefore = entity.getLastRegion();
		Point walkPoint = null, runPoint = null;
		if (entity.getUpdateFlags().teleported())
		{
			reset();
			entity.getPosition().set(entity.getTeleportPosition());
			entity.setTeleportPosition(null);
		} else
		{
			walkPoint = getNextPoint();
			if (isRunning())
				runPoint = getNextPoint();
			int walkDir = walkPoint == null ? -1 : walkPoint.dir;
			int runDir = runPoint == null ? -1 : runPoint.dir;
			entity.setMovementDirections(walkDir, runDir);
		}
		int diffX = entity.getPosition().getX() - posBefore.getRegionX() * 8;
		int diffY = entity.getPosition().getY() - posBefore.getRegionY() * 8;
		boolean changed = false;
		if (diffX < 16)
			changed = true;
		else if (diffX >= 88)
			changed = true;
		if (diffY < 16)
			changed = true;
		else if (diffY >= 88)
			changed = true;
		if (changed)
		{
			entity.getUpdateFlags().flag(Flag.MAP_REGION_CHANGED);
			entity.saveLastRegion();
		}
	}

	public void addStep(int x, int y)
	{
		if (isEmpty())
			reset();
		Point last = waypoints.peekLast();
		int diffX = x - last.x;
		int diffY = y - last.y;
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int i = 0; i < max; i++)
		{
			if (diffX < 0)
				diffX++;
			else if (diffX > 0)
				diffX--;
			if (diffY < 0)
				diffY++;
			else if (diffY > 0)
				diffY--;
			addStepInternal(x - diffX, y - diffY);
		}
	}

	private void addStepInternal(int x, int y)
	{
		if (waypoints.size() >= 50)
			return;
		Point last = waypoints.peekLast();
		int diffX = x - last.x;
		int diffY = y - last.y;
		int dir = DirectionFinder.direction(diffX, diffY);
		if (dir > -1)
			waypoints.add(new Point(x, y, dir));
	}

	private Point getNextPoint()
	{
		Point p = waypoints.poll();
		if (p == null || p.dir == -1)
			return null;
		else
		{
			int diffX = DirectionFinder.DIRECTION_DELTA_X[p.dir];
			int diffY = DirectionFinder.DIRECTION_DELTA_Y[p.dir];
			entity.getPosition().transform(diffX, diffY, entity.getPosition().getZ());
			return p;
		}
	}

	public void finish()
	{
		waypoints.removeFirst();
	}

	public boolean isEmpty()
	{
		return waypoints.isEmpty();
	}

	public void setIsRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
	}

	public void setRunQueue(boolean runQueue)
	{
		this.runQueue = runQueue;
	}

	public boolean isRunning()
	{
		return isRunning || runQueue;
	}

	public boolean runQueueEnabled()
	{
		return runQueue;
	}

	private static class Point
	{

		private final int x;
		private final int y;
		private final int dir;

		public Point(int x, int y, int dir)
		{
			this.x = x;
			this.y = y;
			this.dir = dir;
		}

	}

}
