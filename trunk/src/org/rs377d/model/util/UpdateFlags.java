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

import java.util.BitSet;

import org.rs377d.model.Entity;
import org.rs377d.model.player.Player;

public class UpdateFlags
{

	private int mask = 0;
	private BitSet flags = new BitSet();
	private Entity entity;

	public UpdateFlags(Entity entity)
	{
		this.entity = entity;
	}

	public void reset()
	{
		mask = 0;
		flags.clear();
	}

	public void flag(Flag flag)
	{
		switch (flag)
		{
		case UPDATE:
			flags.set(Flag.UPDATE.val, true);
			break;
		case TELEPORTED:
			flags.set(Flag.TELEPORTED.val, true);
			break;
		case MAP_REGION_CHANGED:
			flags.set(Flag.MAP_REGION_CHANGED.val, true);
			break;
		case ANIMATION:
			mask |= entity instanceof Player ? 0x8 : 0x2;
			flags.set(Flag.UPDATE.val, true);
			flags.set(Flag.ANIMATION.val, true);
			break;
		case CHAT:
			mask |= entity instanceof Player ? 0x40 : 0x20;
			flags.set(Flag.UPDATE.val, true);
			flags.set(Flag.CHAT.val, true);
			break;
		case APPEARANCE:
			// player only
			mask |= 0x4;
			flags.set(Flag.UPDATE.val, true);
			flags.set(Flag.APPEARANCE.val, true);
			break;
		case HIT:
			mask |= entity instanceof Player ? 0x80 : 0x10;
			flags.set(Flag.UPDATE.val, true);
			flags.set(Flag.HIT.val, true);
			break;
		case DIRECTION:
			mask |= entity instanceof Player ? 0x2 : 0x40;
			flags.set(Flag.UPDATE.val, true);
			flags.set(Flag.DIRECTION.val, true);
			break;
		}
	}

	public int getMask()
	{
		return mask;
	}

	public boolean update()
	{
		return flags.get(Flag.UPDATE.val);
	}

	public boolean animation()
	{
		return flags.get(Flag.ANIMATION.val);
	}

	public boolean chat()
	{
		return flags.get(Flag.CHAT.val);
	}

	public boolean appearance()
	{
		return flags.get(Flag.APPEARANCE.val);
	}

	public boolean hit()
	{
		return flags.get(Flag.HIT.val);
	}

	public boolean direction()
	{
		return flags.get(Flag.DIRECTION.val);
	}

	public boolean teleported()
	{
		return flags.get(Flag.TELEPORTED.val);
	}

	public boolean mapRegionChanged()
	{
		return flags.get(Flag.MAP_REGION_CHANGED.val);
	}

	public enum Flag
	{
		UPDATE(0), TELEPORTED(1), MAP_REGION_CHANGED(2), ANIMATION(3), CHAT(4), APPEARANCE(5), HIT(6), DIRECTION(7);

		public final int val;

		Flag(int val)
		{
			this.val = val;
		}

	}

}
