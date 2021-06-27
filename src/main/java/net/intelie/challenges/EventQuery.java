package net.intelie.challenges;

import java.util.TreeSet;
import java.util.HashSet;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantLock;


public class EventQuery implements EventIterator {
	private TreeSet<Event> eventList;// a list of the events of this type and that fits the required startTime and endTime;
	private Event current = null;//the object that is currently being ref'ed by the query;
	private WeakReference<HashSet<Event>> storeRef;	//a reference to the set of the type of the Event;
	private WeakReference<ReentrantLock> typeMutex;// a reference to the lock of events of this type;
	private ReentrantLock queryMutex;// a lock to the query itself, so things that change the eventList can't be ran simultaneously
	
	EventQuery(TreeSet<Event> events,HashSet<Event> reference, ReentrantLock typeMutex){
		this.eventList = events;
		this.storeRef = new WeakReference<HashSet<Event>>(reference);
		this.queryMutex = new ReentrantLock();
		this.typeMutex = new WeakReference<ReentrantLock>(typeMutex);
	}
	
	
	@Override
	public void close() throws Exception {
		//there are no resources to be closed, I could perhaps make the variables null so GC would free them
	}

	@Override
	public boolean moveNext() {
		
		try {
			queryMutex.lock();// lock the EventQuery object to be accessible only by the thread currently executing this method
			
			current = eventList.pollFirst(); // remove the first element from the list of events in the query and assign it to 
				//be the current
			if(current == null){
				return false; // if current is null it means there was no event in the eventList of the query;
			}
			return true;  // there was an event;
		}
		finally {
			queryMutex.unlock(); // unlock the EventQuery to be used by other threads after returning the boolean value
		}
	}
	
	@Override
	
	//return the event that is ref'd by the iterator
	public Event current(){
		
		try{
			queryMutex.lock(); // lock the query to the current thread so the value of "current" can't change by moveNext() method 
				//before accessing;
			if(current != null) {
				return current; // return the Event itself currently ref'd by the iterator
			}
		}
		finally {
			queryMutex.unlock(); // unlock the EventQuery object to be usable by other threads
		}
		throw new IllegalStateException("current() called before moveNext()"); // throw exception if you call the method before 
			//moveNext or if there is no more events in the eventList
	}

	@Override
	
	//remove the value currently ref'd by the iterator from its store.
	public void remove(){
		
		ReentrantLock typeMutexTemp = typeMutex.get(); //try to get the lock variable ref'd by the typeMutex weak reference, 
			//to allow the access to the set of the events of this type;
		
		if(typeMutexTemp == null) {
			return; //it means that all events of this type have been removed somewhere else by EventStore.removeAll
				//method so just do nothing and return;
		}
		
		try{
			typeMutexTemp.lock();//lock all the operations on the events of this type
		
			if(current != null) {
				storeRef.get().remove(current); // remove the Event from the HashSet ref'ed by storeRef.
				return;
			}
		
		}
		finally {
			typeMutexTemp.unlock();//unlock the operations on the events of this type
		}
		
		
		
		throw new IllegalStateException("remove() called before moveNext()");// throw exception if current == null, moveNext() was
			//was not called or the eventList reached the end;

	}

}
