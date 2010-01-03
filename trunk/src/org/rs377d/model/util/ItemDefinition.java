package org.rs377d.model.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

/**
 * The item definition manager.
 * 
 * @author Vastico
 * @author Graham Edgecombe
 */
public class ItemDefinition
{

	private static ItemDefinition[] definitions;

	public static ItemDefinition forId(int id)
	{
		return definitions[id];
	}

	public static void init() throws IOException
	{
		if (definitions != null)
		{
			throw new IllegalStateException("Definitions already loaded.");
		}
		RandomAccessFile raf = new RandomAccessFile("data/itemDefinitions.bin", "r");
		try
		{
			ByteBuffer buffer = raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length());
			int count = buffer.getShort() & 0xFFFF;
			definitions = new ItemDefinition[count];
			for (int i = 0; i < count; i++)
			{
				String name = readString(buffer);
				String examine = readString(buffer);
				boolean noted = buffer.get() == 1 ? true : false;
				int parentId = buffer.getShort() & 0xFFFF;
				if (parentId == 65535)
				{
					parentId = -1;
				}
				boolean noteable = buffer.get() == 1 ? true : false;
				int notedId = buffer.getShort() & 0xFFFF;
				if (notedId == 65535)
				{
					notedId = -1;
				}
				boolean stackable = buffer.get() == 1 ? true : false;
				boolean members = buffer.get() == 1 ? true : false;
				boolean prices = buffer.get() == 1 ? true : false;
				int shop = -1;
				int highAlc = -1;
				int lowAlc = -1;
				if (prices)
				{
					shop = buffer.getInt();
					highAlc = (int) (shop * 0.6D);
					lowAlc = (int) (shop * 0.4D);
				}
				definitions[i] = new ItemDefinition(i, name, examine, noted, noteable, stackable, parentId, notedId, members, shop, highAlc, lowAlc);
			}
		} finally
		{
			raf.close();
		}
	}

	private final int id;
	private final String name;
	private final String examine;
	private final boolean noted;
	private final boolean noteable;
	private final boolean stackable;
	private final int parentId;
	private final int notedId;
	private final boolean members;
	private final int shopValue;
	private final int highAlcValue;
	private final int lowAlcValue;

	private ItemDefinition(int id, String name, String examine, boolean noted, boolean noteable, boolean stackable, int parentId, int notedId, boolean members, int shopValue, int highAlcValue, int lowAlcValue)
	{
		this.id = id;
		this.name = name;
		this.examine = examine;
		this.noted = noted;
		this.noteable = noteable;
		this.stackable = stackable;
		this.parentId = parentId;
		this.notedId = notedId;
		this.members = members;
		this.shopValue = shopValue;
		this.highAlcValue = highAlcValue;
		this.lowAlcValue = lowAlcValue;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return examine;
	}

	public boolean isNoted()
	{
		return noted;
	}

	public boolean isNoteable()
	{
		return noteable;
	}

	public boolean isStackable()
	{
		return stackable || noted;
	}

	public int getNormalId()
	{
		return parentId;
	}

	public int getNotedId()
	{
		return notedId;
	}

	public boolean isMembersOnly()
	{
		return members;
	}

	public int getValue()
	{
		return shopValue;
	}

	public int getLowAlcValue()
	{
		return lowAlcValue;
	}

	public int getHighAlcValue()
	{
		return highAlcValue;
	}

	public static String readString(ByteBuffer buffer)
	{
		StringBuilder bldr = new StringBuilder();
		while (buffer.hasRemaining())
		{
			byte b = buffer.get();
			if (b == 0)
			{
				break;
			}
			bldr.append((char) b);
		}
		return bldr.toString();
	}

}