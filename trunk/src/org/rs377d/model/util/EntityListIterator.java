/*
 * Copyright (C) 2009 Blake Beaupain and Graham Edgecombe
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

package org.rs377d.model.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.rs377d.model.Entity;

/**
 * An implementation of an iterator for an entity list.
 * 
 * @author Graham Edgecombe
 * 
 * @param <E>
 *            The type of entity.
 */
public class EntityListIterator<E extends Entity> implements Iterator<E>
{

	/**
	 * The entities.
	 */
	private Entity[] entities;

	/**
	 * The entity list.
	 */
	private EntityList<E> entityList;

	/**
	 * The previous index.
	 */
	private int lastIndex = -1;

	/**
	 * The current index.
	 */
	private int cursor = 0;

	/**
	 * The size of the list.
	 */
	private int size;

	/**
	 * Creates an entity list iterator.
	 * 
	 * @param entityList
	 *            The entity list.
	 */
	public EntityListIterator(EntityList<E> entityList)
	{
		this.entityList = entityList;
		entities = entityList.toArray(new Entity[0]);
		size = entities.length;
	}

	@Override
	public boolean hasNext()
	{
		return cursor < size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next()
	{
		if (!hasNext())
		{
			throw new NoSuchElementException();
		}
		lastIndex = cursor++;
		return (E) entities[lastIndex];
	}

	@Override
	public void remove()
	{
		if (lastIndex == -1)
		{
			throw new IllegalStateException();
		}
		entityList.remove(entities[lastIndex]);
	}

}