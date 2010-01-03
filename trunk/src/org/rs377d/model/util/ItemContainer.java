package org.rs377d.model.util;

public class ItemContainer
{

	private Item[] items;
	private boolean stacking;

	public ItemContainer(int capacity, boolean stacking)
	{
		items = new Item[capacity];
		this.stacking = stacking;
	}

	public boolean add(Item item)
	{
		if (item.isStackable() || stacking)
		{
			int slot = search(item);
			if (slot != -1)
			{
				item.setAmount(item.getAmount() + 1);
				return true;
			}
		}
		int nextSlot = freeSlot();
		if (nextSlot == -1)
			return false;
		set(nextSlot, item);
		return true;
	}

	public void remove(Item item)
	{
		int slot = search(item);
		if (slot == -1)
			return;
		if (item.isStackable() || stacking && item.getAmount() > 1)
			item.setAmount(item.getAmount() - 1);
		else
			set(slot, null);
	}

	public void clear()
	{
		for (int i = 0; i < items.length; i++)
			items[i] = null;
	}

	public void move(int oldSlot, int newSlot)
	{
		Item swap = items[oldSlot];
		items[oldSlot] = items[newSlot];
		items[newSlot] = swap;
	}

	public void set(int index, Item item)
	{
		items[index] = item;
	}

	public boolean isEmpty()
	{
		return freeSlot() == 0;
	}
	
	public int getSize()
	{
		int size = 0;
		for (int i = 0; i < items.length; i++)
			if (items[i] != null)
				size++;
		return size;
	}

	public int search(Item item)
	{
		for (int i = 0; i < items.length; i++)
			if (items[i] == item)
				return i;
		return -1;
	}

	public boolean isFull()
	{
		return freeSlot() == -1;
	}

	public int freeSlot()
	{
		return search(null);
	}
	
	public Item[] getItems()
	{
		return items;
	}

}
