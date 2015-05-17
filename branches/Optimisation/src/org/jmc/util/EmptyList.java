package org.jmc.util;

import java.util.AbstractList;


/**
 * A lightweight List placeholder that is always empty. 
 */
public class EmptyList<E> extends AbstractList<E>
{
	@Override
	public E get(int index) {
		throw new IndexOutOfBoundsException("Index: "+index+", Size: 0");
	}

	@Override
	public int size() {
		return 0;
	}
}
