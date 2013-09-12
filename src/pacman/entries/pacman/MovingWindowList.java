package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MovingWindowList<T> {
	LinkedList<T> list;
	int sizeCap;
	
	public MovingWindowList() {
		list = new LinkedList<T>();
		sizeCap = 10;
	}
	
	public MovingWindowList(int size) {
		list = new LinkedList<T>();
		this.sizeCap = size;
	}
	
	public void put(T item) {
		if (list.size() == sizeCap) {
			list.pollLast();
		}
		list.addFirst(item); 
	}
	
	public List<T> asList() {
		return (List<T>) list.clone(); 
	}
	
	public int size() {
		return list.size();
	}
}
