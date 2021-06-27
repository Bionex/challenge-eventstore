package net.intelie.challenges;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;



public class EventStorage implements EventStore {

    private HashMap<String,LinkedHashSet<Event>> storage;
    private HashMap<String, ReentrantLock> locks;
    private ReentrantLock mainMutex;
	
    public EventStorage() {
    	this.storage = new HashMap<String, LinkedHashSet<Event>>();
    	this.locks = new HashMap<String, ReentrantLock>();
    	this.mainMutex = new ReentrantLock();
    	
    }
    
	@Override
	public void insert(Event event) {
		String eventType = event.type();
		
		mainMutex.lock();
		
		if (!storage.containsKey(eventType)) {
			LinkedHashSet<Event> eventSet = new LinkedHashSet<Event>();
			storage.put(eventType, eventSet);
			locks.put(eventType, new ReentrantLock());
		}
		
		mainMutex.unlock();
		
		ReentrantLock mutex = locks.get(eventType);
		
		mutex.lock();
		
		storage.get(eventType).add(event);
		
		mutex.unlock();
	}

	@Override
	
	//remove all Events of a type
	public void removeAll(String type) {
		

		mainMutex.lock();
		
		if( !storage.containsKey(type)) {
			mainMutex.unlock();
			return;
		}
		
		ReentrantLock mutex = locks.get(type);
		
		mutex.lock();
		
		storage.remove(type);
		locks.remove(type);
		
		mutex.unlock();
		mainMutex.unlock();
	}

	@Override
	public EventIterator query(String type, long startTime, long endTime) {
		
		try {
			mainMutex.lock();
			if(!storage.containsKey(type))
				return null;
		}
		
		finally {
			mainMutex.unlock();
		}
		
		ReentrantLock mutex = locks.get(type);
		LinkedHashSet<Event> events = storage.get(type);
		
		return new EventQuery(events, mutex, startTime, endTime);
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


