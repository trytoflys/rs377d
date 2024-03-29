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

package org.rs377d;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerExecutor
{

	private static ScheduledExecutorService logicExecutor;
	private static ThreadPoolExecutor threadPool;

	static
	{
		logicExecutor = Executors.newSingleThreadScheduledExecutor();
		threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}

	public static ScheduledExecutorService getLogicExecutor()
	{
		return logicExecutor;
	}

	public static ThreadPoolExecutor getThreadPool()
	{
		return threadPool;
	}

}
