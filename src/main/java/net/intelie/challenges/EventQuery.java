package net.intelie.challenges;

import java.util.TreeSet;
import java.util.HashSet;
import java.lang.ref.WeakReference;


public class EventQuery implements EventIterator {
	private TreeSet<Event> eventList;
	private Event current = null;
	private WeakReference<HashSet<Event>> storeRef;
	
	EventQuery(TreeSet<Event> events,HashSet<Event> reference){
		eventList = events;
		storeRef = new WeakReference<HashSet<Event>>(reference);
	}
	
	
	@Override
	public void close() throws Exception {
		eventList = null;
		current = null;
		storeRef = null;

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
	
	//return the event that is ref'd by the iterator
	public Event current(){
		if(current != null) {
			return current;
		}
		
		throw new IllegalStateException("current() called before moveNext()");
	}

	@Override
	public void remove(){
		if(current != null) {
			storeRef.get().remove(current);
			return;
		}
		
		throw new IllegalStateException("remove() called before moveNext()");

	}

}
