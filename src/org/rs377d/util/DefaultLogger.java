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

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultLogger extends PrintStream
{

	private DefaultTimer timer = new DefaultTimer();
	private Date cachedDate = new Date();
	private DateFormat dateFormat = new SimpleDateFormat();

	public DefaultLogger(OutputStream out)
	{
		super(out);
	}

	@Override
	public void print(String msg)
	{
		super.print(getPrefix() + msg);
	}

	private String getPrefix()
	{
		if (timer.elapsed() > 60000)
		{
			timer.reset();
			cachedDate = new Date();
		}
		return "[" + dateFormat.format(cachedDate) + "][" + Thread.currentThread().getName() + "]: ";
	}

}
