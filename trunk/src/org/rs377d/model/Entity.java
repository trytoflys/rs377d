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

import org.rs377d.Configuration;
import org.rs377d.model.util.Position;
import org.rs377d.model.util.UpdateFlags;
import org.rs377d.model.util.WalkingManager;
import org.rs377d.model.util.UpdateFlags.Flag;

public abstract class Entity
{

	private Position lastRegion;
	private WalkingManager walkingManager;
	private Position teleportPosition;
	private UpdateFlags updateFlags;
	private Position position;
	private int walkDir, runDir;
	private int index = -1;

	public Entity()
	{
		updateFlags = new UpdateFlags(this);
		position = new Position(Configuration.DEFAULT_X, Configuration.DEFAULT_Y, Configuration.DEFAULT_Z);
		walkingManager = new WalkingManager(this);
		lastRegion = new Position(position.getX(), position.getY(), position.getZ());
	}

	public void saveLastRegion()
	{
		lastRegion = new Position(position.getX(), position.getY(), position.getZ());
	}

	public Position getLastRegion()
	{
		return lastRegion;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public int getIndex()
	{
		return index;
	}

	public WalkingManager getWalkingManager()
	{
		return walkingManager;
	}

	public void setMovementDirections(int walkDir, int runDir)
	{
		this.walkDir = walkDir;
		this.runDir = runDir;
	}

	public int getWalkDirection()
	{
		return walkDir;
	}

	public int getRunDirection()
	{
		return runDir;
	}

	public void setTeleportPosition(Position teleportPosition)
	{
		updateFlags.flag(Flag.TELEPORTED);
		this.teleportPosition = teleportPosition;
	}

	public Position getTeleportPosition()
	{
		return teleportPosition;
	}

	public UpdateFlags getUpdateFlags()
	{
		return updateFlags;
	}

	public Position getPosition()
	{
		return position;
	}

}
