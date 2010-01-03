package org.rs377d.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class PythonImpl
{

	private static PythonInterpreter py = new PythonInterpreter();
	
	static
	{
		py.setOut(System.out);
	}

	public static void func(String identifier, Object... args)
	{
		try
		{
			PyFunction func = (PyFunction) py.get(identifier);
			if (func == null)
				return;
			PyObject[] funcArgs = new PyObject[args.length];
			for (int i = 0; i < args.length; i++)
				funcArgs[i] = Py.java2py(args[i]);
			func.__call__(funcArgs);
		} catch (PyException ex)
		{
			ex.printStackTrace();
			System.err.println("Unable to call script fucntion: " + identifier);
		}
	}

	public static void loadScripts(File dir)
	{
		for (File child : dir.listFiles())
		{
			if (child.isDirectory())
				loadScripts(child);
			else
			{
				if (!child.getName().endsWith(".py"))
					continue;
				try
				{
					py.execfile(new FileInputStream(child));
					System.out.println("\tLoaded script: " + child.getName());
				} catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

}
