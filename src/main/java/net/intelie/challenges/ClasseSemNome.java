package net.intelie.challenges;

import java.util.TreeSet;

public class ClasseSemNome implements EventIterator {
	private TreeSet<Event> eventList;
	private Event current = null;
	
	
	ClasseSemNome(TreeSet<Event> events){
		eventList = events;
	}
	
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean moveNext() {
		current = eventList.pollFirst();
		if(current == null){
			return false;
		}
		return true;
	}
	
	@Override
	public Event current() {
		return current;
	}

	@Override
	public void remove() {
		current = null;

	}

}
