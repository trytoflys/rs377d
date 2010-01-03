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

import java.io.File;
import java.math.BigInteger;

public class Configuration
{

	public static int MAX_NPCS = 2000;
	public static int TICK_RATE = 600;
	public static int MAX_PLAYERS = 2000;
	public static String HOST = "127.0.0.1";
	public static int PORT = 43594;
	public static BigInteger RSA_D;
	public static BigInteger RSA_N;
	public static int DEFAULT_X = 3200;
	public static int DEFAULT_Y = 3200;
	public static int DEFAULT_Z = 0;
	public static File SCRIPTS_DIR = new File("./scripts");
	public static String RSA_KEY_FILE = new String("./data/private.key");

}
