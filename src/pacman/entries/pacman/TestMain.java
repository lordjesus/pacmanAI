package pacman.entries.pacman;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class TestMain {

	public static void main(String[] args)
	{
		System.out.println("Test");
		
		Stack<Integer> q = new Stack<>();
		
		for (int i = 10; i > 0; i--) {
			q.push(i);
		}
		
		while (!q.isEmpty()) {
			System.out.println(q.pop() + "");
		}
	}
}
