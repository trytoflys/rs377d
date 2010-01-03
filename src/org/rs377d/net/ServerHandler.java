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

package org.rs377d.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.firewall.ConnectionThrottleFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.rs377d.Configuration;
import org.rs377d.ServerExecutor;
import org.rs377d.model.World;
import org.rs377d.model.player.Player;
import org.rs377d.net.codec.Rs2CodecFactory;
import org.rs377d.net.event.EventListenerChain;
import org.rs377d.net.event.EventListenerManager;

public class ServerHandler extends IoHandlerAdapter
{

	private IoAcceptor acceptor;
	private static ServerHandler singleton;

	public ServerHandler()
	{
		acceptor = new NioSocketAcceptor();
	}

	public void start() throws IOException
	{
		acceptor.setHandler(this);
		acceptor.bind(new InetSocketAddress(Configuration.HOST, Configuration.PORT));
		acceptor.getFilterChain().addFirst("throttleFilter", new ConnectionThrottleFilter());
		acceptor.getFilterChain().addLast("protocol", new ProtocolCodecFilter(Rs2CodecFactory.LOGIN));
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable t)
	{
		t.printStackTrace();
		session.close(false);
	}

	@Override
	public void messageReceived(final IoSession session, Object message)
	{
		final Rs2Packet packet = (Rs2Packet) message;
		ServerExecutor.getLogicExecutor().submit(new Runnable()
		{
			@Override
			public void run()
			{
				Thread.currentThread().setName("EventProcessor");
				EventListenerChain chain = EventListenerManager.getPacketHandlerChain(packet);
				chain.prepareChain();
				try
				{
					chain.getNext().handle(session, packet, chain);
				} catch (Exception ex)
				{
					ex.printStackTrace();
					session.close(false);
				}
			}
		});
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception
	{
		session.close(false);
	}

	@Override
	public void sessionOpened(IoSession session)
	{
		System.out.println("Accepting connection from: " + session.getRemoteAddress());
	}

	@Override
	public void sessionClosed(final IoSession session)
	{
		ServerExecutor.getLogicExecutor().submit(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("Closing connection from: " + session.getRemoteAddress());
				Player player = (Player) session.getAttribute("player");
				if (player == null)
					return;
				World.getSingleton().unregisterPlayer(player);
			}
		});
	}

	public static ServerHandler getSingleton()
	{
		return singleton == null ? singleton = new ServerHandler() : singleton;
	}

}
