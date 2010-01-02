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

package org.rs377d.net.event;

public class EventListenerChain
{
	
	private int offset = 0;
	private EventListener[] chain = new EventListener[32];

	public void prepareChain()
	{
		offset = 0;
	}

	public void add(EventListener listener)
	{
		chain[offset++] = listener;
	}
	
	public boolean hasNext()
	{
		return chain[offset + 1] != null;
	}

	public EventListener getNext()
	{
		return chain[offset++];
	}

}
