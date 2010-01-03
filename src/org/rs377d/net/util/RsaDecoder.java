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

package org.rs377d.net.util;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;

import org.rs377d.Configuration;

public class RsaDecoder
{

	public static void loadRSAKeys()
	{
		try
		{
			ObjectInputStream oin = new ObjectInputStream(new FileInputStream("./data/private.key"));
			Configuration.RSA_N = (BigInteger) oin.readObject();
			Configuration.RSA_D = (BigInteger) oin.readObject();
		} catch (Exception ex)
		{
			System.err.println("Error loading private RSA key! Shutting down.");
			System.exit(1);
		}
	}

	public static byte[] decode(byte[] data)
	{
		return new BigInteger(data).modPow(Configuration.RSA_D, Configuration.RSA_N).toByteArray();
	}

}
