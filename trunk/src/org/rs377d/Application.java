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

import java.util.concurrent.TimeUnit;

import org.rs377d.model.util.ItemDefinition;
import org.rs377d.net.ServerHandler;
import org.rs377d.net.update.PlayerUpdateCoordinator;
import org.rs377d.net.util.RsaDecoder;
import org.rs377d.util.DefaultLogger;

public class Application
{

	public static void main(String[] args)
	{
		System.setOut(new DefaultLogger(System.out));
		System.out.println("Starting RuneScape 377 Daemon...");
		// System.out.println("Loading Python scripts...");
		try
		{
			ItemDefinition.init();
			// PythonImpl.loadScripts(Configuration.SCRIPTS_DIR);
			// PythonImpl.func("configure");
			ServerExecutor.getLogicExecutor().scheduleAtFixedRate(new PlayerUpdateCoordinator(), 0, Configuration.TICK_RATE, TimeUnit.MILLISECONDS);
			RsaDecoder.loadRSAKeys();
			ServerHandler.getSingleton().start();
			System.out.println("Running.");
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
