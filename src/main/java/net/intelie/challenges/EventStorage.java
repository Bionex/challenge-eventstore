package net.intelie.challenges;

import java.util.TreeSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;


public class EventStorage implements EventStore {

    private HashMap<String,HashSet<Event>> storage;
    private HashMap<String, ReentrantLock> locks;
    private ReentrantLock mainMutex;
	
    public EventStorage() {
    	this.storage = new HashMap<String, HashSet<Event>>();
    	this.locks = new HashMap<String, ReentrantLock>();
    	this.mainMutex = new ReentrantLock();
    	
    }
    
	@Override
	public void insert(Event event) {
		String eventType = event.type();
		
		mainMutex.lock();
		
		if (!storage.containsKey(eventType)) {
			HashSet<Event> eventSet = new HashSet<Event>();
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
		TreeSet<Event> iterableElements = new TreeSet<Event>();
		int fitQuantity = 0;
		
		
		try {
			mainMutex.lock();
			if(!storage.containsKey(type))
				return null;
		}
		
		finally {
			mainMutex.unlock();
		}
		
		ReentrantLock mutex = locks.get(type);
		
		mutex.lock();
		HashSet<Event> searchList = storage.get(type);		
		Iterator<Event> iter = searchList.iterator();
		
		while(iter.hasNext()) {
			Event e = (Event) iter.next();
			long eventTime = e.timestamp();
			
			if(eventTime <= endTime && eventTime >= startTime) {
				iterableElements.add(e);
				fitQuantity += 1;
			}
		}
		
		mutex.unlock();
		
		if(fitQuantity == 0)
			return null;
		
		return new EventQuery(iterableElements, searchList, mutex);
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


