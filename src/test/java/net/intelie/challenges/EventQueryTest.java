package net.intelie.challenges;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EventQueryTest {

	Event expected[] = new Event[3];
	Event anotherEvent = new Event("other_type", 50L);
	Event result = null;
	EventStorage storage = new EventStorage();
	EventIterator query;
	boolean moveNextResult;
	
	@Before
	public void setUp(){
		expected[0] = new Event("some_type", 123L);
		expected[1] = new Event("some_type", 124L);
		expected[2] = new Event("some_type", 125L);
		
		storage.insert(expected[0]);
		storage.insert(expected[1]);
		storage.insert(expected[2]);
		storage.insert(anotherEvent);
		
		
	}
	
	@Test
	public void moveNextTest() {

		query = storage.query("some_type", 123L, 124L);
		
		moveNextResult = query.moveNext();//there is one element in the query so must return true;
		assertTrue(moveNextResult);
		
		result = query.current();
		assertSame(expected[0], result); // the object at current position must be the unique object inserted that fit the arguments;
		
		moveNextResult = query.moveNext(); //there is no more elements in the query, should return false;
		assertFalse(moveNextResult);
	}
	
	@Test
	public void currentTest() {
		//shows normal current() behavior
		query = storage.query("some_type", 123L, 126L);
		
		int counter = 0;
		while(query.moveNext()) {
			result = query.current();
			assertSame(result,expected[counter]); // the queried events must be the same object that have been inserted before
			counter += 1;
		}
	}
	
	@Test
	public void currentTest2() {
		//shows current() if there was no move next
		query = storage.query("some_type", 123L, 125L);
		
		try {
			result = query.current();
		}
		catch(IllegalStateException e) {
			assertEquals(e.getMessage(), "current() called before moveNext()");
		}
	}
	
	@Test
	public void removeTest() {
		// normal remove() behavior
		query = storage.query("some_type", 123L, 200L);
		
		int counter = 0;
		while(query.moveNext()) {
			result = query.current();
			if(counter == 1) {
				query.remove();//remove the event expected[1] aka event("some_type", 124L);
			}
			assertSame(result,expected[counter]); // the queried events must be the same object that have been inserted before
			counter += 1;
		}
		
		
		query = storage.query("some_type", 123L, 125L);// a new query to get the events that are on the store currently;
		
		counter = 0;
		while(query.moveNext()) {
			result = query.current();
			if(counter > 0) {
				assertSame(result,expected[counter + 1]);// all the others elements have been moved 1 slot to the left because
					// the event at index 1 have been removed;
			}
			else {
				assertSame(result, expected[counter]); //the first element of the query is the same
			}
			counter += 1;
		}
	}
	
	@Test
	public void removeTest2() {
		//remove() behavior if called before moveNext()
		
		query = storage.query("some_type", 123L, 125L);
		
		try {
			query.remove();
		}
		catch(IllegalStateException e) {
			assertEquals(e.getMessage(), "remove() called before moveNext()");
		}
	}

}
