package net.intelie.challenges;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;

import static org.junit.Assert.assertNotNull;

/*
if(query != null) {
	int counter = 0;
	while(query.moveNext()) {
		
		result = query.current();
		if(counter != 2)
			assertEquals(expected[counter], result);			
		counter = counter + 1;
		
		
	}
	return;
}

*/

public class EventStorageTest {
	
	
	Event expected[] = new Event[3];
	Event anotherEvent = new Event("other_type", 50L);
	Event result = null;
	EventStorage storage = new EventStorage();
	EventIterator query;
	
	@Before
	public void setUp() {
		expected[0] = new Event("some_type", 123L);
		expected[1] = new Event("some_type", 124L);
		expected[2] = new Event("some_type", 125L);
	}
	
	
	@Test
	public void insertAndQueryTest() {
		
		storage.insert(expected[0]);
		
		query = storage.query("some_type", 123L, 123);
		
		while(query.moveNext()) {
			result = query.current();
		}
		
		assertSame(expected[0], result);
		assertEquals(expected[0].timestamp(), result.timestamp());
		assertEquals(expected[0].type(), result.type());
		
		return;
	}

	
	@Test
	public void removeAllTest() {
		//this tests that all events of a type is deleted on a removeAll(type) call;
		storage.insert(expected[0]);
		storage.insert(expected[1]);
		storage.insert(expected[2]);
		
		
		storage.removeAll("some_type"); // remove all the elements so the query method returns null;
		
		query = storage.query("some_type", 123L, 124L);
		
		assertNull(query);
	}
	
	@Test
	public void removeAllTest2() {
		//this tests that an event of a type should still persist if a call to removeAll(another type) have been done;
		storage.insert(expected[0]);
		storage.insert(expected[1]);
		storage.insert(expected[2]);
		storage.insert(anotherEvent);
		
		
		storage.removeAll("some_type");
		
		query = storage.query("some_type", 123L, 124L);
		assertNull(query);// query of the elements removed is null;
		
		query = storage.query("other_type", 0L, 100L);
		assertNotNull(query);//there is a element, query must not be null;
		
		while(query.moveNext()) {
			
			result = query.current();
			assertSame(anotherEvent, result);// the objects must be the same
			
			
		}
	}
	
	@Test
	public void queryTest() {
		//this methods shows that only the events that fit the query arguments are in the query and the query is sorted by
		//timestamp in ascending order;
		
		
		storage.insert(expected[0]);
		storage.insert(anotherEvent);
		storage.insert(expected[2]);
		storage.insert(expected[1]);
		
		query = storage.query("some_type", 123L, 124L);
		
		int counter = 0;
		while(query.moveNext()) {
			result = query.current();
			assertSame(expected[counter], result);
			counter += 1;
		}//expected[2] aka event("some_type", 125L) is not in the query cause it doesn't fit the parameters;
		
	}
}
