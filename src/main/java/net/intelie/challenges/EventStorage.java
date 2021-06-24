package net.intelie.challenges;

//import java.util.AbstractMap;
//import java.util.SortedSet;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class EventStorage implements EventStore {

    private HashMap<String,HashSet<Event>> storage;
	
    public EventStorage() {
    	this.storage = new HashMap<String, HashSet<Event>>();
    	
    }
    
	@Override
	public void insert(Event event) {
		String eventType = event.type();
		if (!storage.containsKey(eventType)) {
			HashSet<Event> eventSet = new HashSet<Event>();
			storage.put(eventType, eventSet);
		}
		storage.get(eventType).add(event);
	}

	@Override
	
	//remove all Events of a type
	public void removeAll(String type) {
		storage.remove(type);
	}

	@Override
	public EventIterator query(String type, long startTime, long endTime) {
		TreeSet<Event> iterableElements = new TreeSet<Event>();
		if(!storage.containsKey(type))
			return null;
			HashSet<Event> searchList = storage.get(type);
		
		Iterator<Event> iter = searchList.iterator();
		
		while(iter.hasNext()) {
			Event e = (Event) iter.next();
			long eventTime = e.timestamp();
			
			if(eventTime <= endTime && eventTime >= startTime) {
				iterableElements.add(e);
			}
			
		}
		
		
		return null;
	}
	
	public void showAll() {
		Set<String> allKeys = storage.keySet();
		
		for(String type : allKeys) {
			Iterator<Event> iter = storage.get(type).iterator();
			while(iter.hasNext()) {
				Event e = (Event) iter.next();
				System.out.println(e.type() + " " + e.timestamp());
			}
		}
		
		return;
	}
}


