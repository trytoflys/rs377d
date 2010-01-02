package org.rs377d.model.npc;

import org.rs377d.model.Entity;

public class Npc extends Entity
{
	
	private int id;
	
	public Npc(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return id;
	}

}
