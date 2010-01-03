package org.rs377d.model.util;

/**
 * Represents a single item.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Item
{

	private int id;
	private int count;

	public Item(int id)
	{
		this(id, 1);
	}

	public Item(int id, int count)
	{
		if (count < 0)
		{
			throw new IllegalArgumentException("Count cannot be negative.");
		}
		this.id = id;
		this.count = count;
	}

	public ItemDefinition getDefinition()
	{
		return ItemDefinition.forId(id);
	}

	public int getId()
	{
		return id;
	}

	public int getCount()
	{
		return count;
	}

	@Override
	public String toString()
	{
		return Item.class.getName() + " [id=" + id + ", count=" + count + "]";
	}

}