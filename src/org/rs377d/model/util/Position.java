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

public class Position
{

	private int x, y, z;

	public Position(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(Position position)
	{
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
	}

	public void transform(int x, int y, int z)
	{
		this.x = this.x + x;
		this.y = this.y + y;
		this.z = this.z + z;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}

	public int getLocalX()
	{
		return getLocalX(this);
	}

	public int getLocalY()
	{
		return getLocalY(this);
	}

	public int getLocalX(Position p)
	{
		return x - 8 * p.getRegionX();
	}

	public int getLocalY(Position p)
	{
		return y - 8 * p.getRegionY();
	}

	public int getRegionX()
	{
		return (x >> 3) - 6;
	}

	public int getRegionY()
	{
		return (y >> 3) - 6;
	}

	public boolean isWithinDistance(Position other)
	{
		if (z != other.z)
		{
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}

}
