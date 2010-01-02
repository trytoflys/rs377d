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

import org.apache.mina.core.buffer.IoBuffer;

public class Rs2ProtocolUtils
{

	public static int[] packetSizes = new int[256];

	static
	{
		/*
		 * this is from a 377 whitescape.. it differs quite a bit from the
		 * clients version though?
		 */
		for (int i = 0; i < packetSizes.length; i++)
		{
			packetSizes[i] = 0;
		}
		packetSizes[1] = 12;
		packetSizes[3] = 6;
		packetSizes[4] = 6;
		packetSizes[6] = 0;
		packetSizes[8] = 2;
		packetSizes[13] = 2;
		packetSizes[19] = 4;
		packetSizes[22] = 2;
		packetSizes[24] = 6;
		packetSizes[28] = -1;
		packetSizes[31] = 4;
		packetSizes[36] = 8;
		packetSizes[40] = 0;
		packetSizes[42] = 2;
		packetSizes[45] = 2;
		packetSizes[49] = -1;
		packetSizes[50] = 6;
		packetSizes[54] = 6;
		packetSizes[55] = 6;
		packetSizes[56] = -1;
		packetSizes[57] = 8;
		packetSizes[67] = 2;
		packetSizes[71] = 6;
		packetSizes[75] = 4;
		packetSizes[77] = 6;
		packetSizes[78] = 4;
		packetSizes[79] = 2;
		packetSizes[80] = 2;
		packetSizes[83] = 8;
		packetSizes[91] = 6;
		packetSizes[95] = 4;
		packetSizes[100] = 6;
		packetSizes[104] = 4;
		packetSizes[110] = 0;
		packetSizes[112] = 2;
		packetSizes[116] = 2;
		packetSizes[119] = 1;
		packetSizes[120] = 8;
		packetSizes[123] = 7;
		packetSizes[126] = 1;
		packetSizes[136] = 6;
		packetSizes[140] = 4;
		packetSizes[141] = 8;
		packetSizes[143] = 8;
		packetSizes[152] = 12;
		packetSizes[157] = 4;
		packetSizes[158] = 6;
		packetSizes[160] = 8;
		packetSizes[161] = 6;
		packetSizes[163] = 13;
		packetSizes[165] = 1;
		packetSizes[168] = 0;
		packetSizes[171] = -1;
		packetSizes[173] = 3;
		packetSizes[176] = 3;
		packetSizes[177] = 6;
		packetSizes[181] = 6;
		packetSizes[184] = 10;
		packetSizes[187] = 1;
		packetSizes[194] = 2;
		packetSizes[197] = 4;
		packetSizes[202] = 0;
		packetSizes[203] = 6;
		packetSizes[206] = 8;
		packetSizes[210] = 8;
		packetSizes[211] = 12;
		packetSizes[213] = -1;
		packetSizes[217] = 8;
		packetSizes[222] = 3;
		packetSizes[226] = 2;
		packetSizes[227] = 9;
		packetSizes[228] = 6;
		packetSizes[230] = 6;
		packetSizes[231] = 6;
		packetSizes[233] = 2;
		packetSizes[241] = 6;
		packetSizes[244] = -1;
		packetSizes[245] = 2;
		packetSizes[247] = -1;
		packetSizes[248] = 0;
	}

	public static long playerNameToLong(String s)
	{
		long l = 0L;
		for (int i = 0; i < s.length() && i < 12; i++)
		{
			char c = s.charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L)
			l /= 37L;
		return l;
	}

	public static String getRs2String(IoBuffer buf)
	{
		StringBuilder bldr = new StringBuilder();
		byte b;
		while (buf.hasRemaining() && (b = buf.get()) != '\n')
			bldr.append((char) b);
		return bldr.toString();
	}

}
