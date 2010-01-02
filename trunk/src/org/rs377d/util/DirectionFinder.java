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

package org.rs377d.util;

public class DirectionFinder
{

	public static final byte[] DIRECTION_DELTA_X = new byte[]
	{ -1, 0, 1, -1, 1, -1, 0, 1 };

	public static final byte[] DIRECTION_DELTA_Y = new byte[]
	{ 1, 1, 1, 0, 0, -1, -1, -1 };

	public static int direction(int dx, int dy)
	{
		if (dx < 0)
		{
			if (dy < 0)
			{
				return 5;
			} else if (dy > 0)
			{
				return 0;
			} else
			{
				return 3;
			}
		} else if (dx > 0)
		{
			if (dy < 0)
			{
				return 7;
			} else if (dy > 0)
			{
				return 2;
			} else
			{
				return 4;
			}
		} else
		{
			if (dy < 0)
			{
				return 6;
			} else if (dy > 0)
			{
				return 1;
			} else
			{
				return -1;
			}
		}
	}

}
