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

package org.rs377d.net.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class Rs2CodecFactory implements ProtocolCodecFactory
{

	private Rs2Encoder encoder = new Rs2Encoder();
	private Rs2Decoder decoder = new Rs2Decoder();
	private Rs2LoginDecoder loginDecoder = new Rs2LoginDecoder();
	private boolean loginFactory = false;
	public static Rs2CodecFactory LOGIN = new Rs2CodecFactory(true);
	public static Rs2CodecFactory GAME = new Rs2CodecFactory(false);

	public Rs2CodecFactory(boolean loginFactory)
	{
		this.loginFactory = loginFactory;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception
	{
		return loginFactory ? loginDecoder : decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception
	{
		return encoder;
	}

}
