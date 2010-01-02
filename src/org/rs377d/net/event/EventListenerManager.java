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

import org.rs377d.net.Rs2Packet;
import org.rs377d.net.event.impl.ChatMuteFilter;
import org.rs377d.net.event.impl.ChatEventListener;
import org.rs377d.net.event.impl.DefaultEventListener;
import org.rs377d.net.event.impl.QuietEventListener;
import org.rs377d.net.event.impl.WalkingEventFilter;
import org.rs377d.net.event.impl.WalkingEventListener;

public class EventListenerManager
{

	private static EventListenerChain[] chains = new EventListenerChain[256];

	public static EventListenerChain getPacketHandlerChain(Rs2Packet packet)
	{
		return chains[packet.getOpcode()];
	}

	static
	{
		EventListenerChain defaultChain = new EventListenerChain();
		defaultChain.add(new DefaultEventListener());
		
		EventListenerChain quietChain = new EventListenerChain();
		quietChain.add(new QuietEventListener());
		
		EventListenerChain chatChain = new EventListenerChain();
		chatChain.add(new ChatMuteFilter());
		chatChain.add(new ChatEventListener());
		
		EventListenerChain walkChain = new EventListenerChain();
		walkChain.add(new WalkingEventFilter());
		walkChain.add(new WalkingEventListener());
		
		chains[40] = quietChain;
		chains[248] = quietChain;
		chains[19] = quietChain;
		chains[140] = quietChain;
		chains[187] = quietChain;
		
		chains[49] = chatChain;
		
		chains[213] = walkChain;
		chains[28] = walkChain;
		chains[247] = walkChain;
		
		for (int i = 0; i < chains.length; i++)
			if (chains[i] == null)
				chains[i] = defaultChain;
	}

}
