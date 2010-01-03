package org.rs377d.model.npc;

import java.util.ArrayList;
import java.util.List;

import org.rs377d.model.Entity;

public class Npc extends Entity
{

	private int id;
	private List<Integer[]> locations = new ArrayList<Integer[]>();

	public Npc(int id)
	{
		this.id = id;
	}

	public int getID()
	{
		return id;
	}
	
	public List<Integer[]> getWalkableLocations()
	{
		return locations;
	}

	public void setWalkableLocations(int minX, int minY, int maxX, int maxY)
	{
		int countX = maxX - minX;
		int countY = maxY - minY;
		for (int x = 0; x < countX; x++)
		{
			for (int y = 0; y < countY; y++)
			{
				int locX = minX + x;
				int locY = minY + y;
				locations.add(new Integer[]
				{ locX, locY });
			}
		}

	}

}
