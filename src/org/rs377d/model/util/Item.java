package org.rs377d.model.util;

public class Item
{
	
	private int id, amount;
	private boolean stackable;
	
	public Item(int id, int amount, boolean stackable)
	{
		this.id = id;
		this.amount = amount;
		this.stackable = stackable;
	}
	
	public int getID()
	{
		return id;
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public void setAmount(int amount)
	{
		this.amount = amount;
	}
	
	public boolean isStackable()
	{
		return stackable;
	}

}
