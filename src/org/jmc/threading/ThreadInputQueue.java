package org.jmc.threading;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

public class ThreadInputQueue {
	private Queue<Point> inputQueue = new LinkedList<Point>();
	private boolean finished = false;
	
	public synchronized void add(Point p){
		inputQueue.add(p);
		notify();
	}

	public synchronized Point getNext() throws InterruptedException {
		while (true) {
			if (!inputQueue.isEmpty()) {
				return inputQueue.remove();
			} else if (finished) {
				return null;
			}
			wait();
		}
	}
	
	public synchronized void clear() {
		inputQueue.clear();
	}
	
	public synchronized int size() {
		return inputQueue.size();
	}
	
	public synchronized void finish(){
		finished = true;
		notifyAll();
	}
}
