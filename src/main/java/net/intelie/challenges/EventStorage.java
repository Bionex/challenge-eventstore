package net.intelie.challenges;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.locks.ReentrantLock;



public class EventStorage implements EventStore {

    private HashMap<String,LinkedHashSet<Event>> storage; // stores all the events of a given type in a linkedhashset
    	//to keep insertion order, and the hashmap links the type to the linkedhashset itself: 
    private HashMap<String, ReentrantLock> locks;//a list of locks to lock the access to the linkedhashset of a given event type
    private ReentrantLock mainMutex; // the lock to the access/changes of the mapping itself, if anything must change/make sure it don't change
    	// then this lock must be used
	
    public EventStorage() {
    	this.storage = new HashMap<String, LinkedHashSet<Event>>();
    	this.locks = new HashMap<String, ReentrantLock>();
    	this.mainMutex = new ReentrantLock();
    	
    }
    
	@Override
	//insert the Event into the store
	public void insert(Event event) {
		String eventType = event.type();
		
		mainMutex.lock();//lock the access of the others threads to the map
		
		if (!storage.containsKey(eventType)) {//if the map does not contains the key, we must create it
			LinkedHashSet<Event> eventSet = new LinkedHashSet<Event>();
			storage.put(eventType, eventSet);
			locks.put(eventType, new ReentrantLock());//create a lock to the new linkedhashset that have been inserted into the map
				//at the key eventType
		}
		
		mainMutex.unlock();//unlock the access of the others threads to the map
		
		ReentrantLock mutex = locks.get(eventType);//get the lock of the eventType
		
		mutex.lock();//lock the access to the linkedhashset of eventType
		
		storage.get(eventType).add(event);//insert the event into the linkedhashset
		
		mutex.unlock();//unlock the access to the linkedhashset
	}

	@Override
	
	//remove all Events of a type
	public void removeAll(String type) {
		//O(1) operation since we store all the events of the same type together
		

		mainMutex.lock();//lock the access of the others threads to the map
		
		if( !storage.containsKey(type)) {
			mainMutex.unlock(); // if there is no key for events of this type, then there is event of such a type in the store
				// then just unlock the access to the map and return
			return;
		}
		
		//so we found a key, we must remove the events now
		
		ReentrantLock mutex = locks.get(type); //get the lock of the type passed to the method, it must exist if exist a key
			//with this type in the map
		
		mutex.lock();//lock the linkedhashset of the type passed to the method
		
		storage.remove(type); //remove map of the type to the linkedhashset, making it elegible to Garbage Collector
			//because there are no more references to the linkedhashset, all the events will also be destroyed during this process
		
		locks.remove(type);//there will be no more events of this type at the moment, so lock must be removed as well
		
		mutex.unlock();//unlock the lock of this type(even tought its removed from the locks list, its still valid cause its
			//being referenced by mutex local variable
		
		mainMutex.unlock();//finally unlock the access to the map
	}

	@Override
	public EventIterator query(String type, long startTime, long endTime) {
		
		try {
			mainMutex.lock();//lock the access to the map
			if(!storage.containsKey(type))
				return null;//return null if there is no key of the given type in the map
		}
		
		finally {
			mainMutex.unlock();//unlock the access to the map again
		}
		
		ReentrantLock mutex = locks.get(type); //get a reference to the lock of the given type
		LinkedHashSet<Event> events = storage.get(type);//get a reference to the linkedhashset of the given type 
		
		//return a EventQuery object that implement the required behavior
		return new EventQuery(events, mutex, startTime, endTime);
	}
}


