package net.intelie.challenges;

public class Main {
	static int testAmount = 3;
	
	public static void main(String[] args) {
		Event eventList[] = new Event[testAmount];
		EventStorage storage = new EventStorage();
		
		
		for( int i = 0; i < testAmount; i++) {
			eventList[i] = new Event("venda", (100 * i + i/2));
		}
		
		for(Event event: eventList) {
			storage.insert(event);
		}
		
		for( int i = 0; i < testAmount; i++) {
			eventList[i] = new Event("compra", (100 * i + i/2));
		}
		
		for(Event event: eventList) {
			storage.insert(event);
		}
		
		for( int i = 0; i < testAmount; i++) {
			eventList[i] = new Event("extracao", (100 * i + i/2));
		}
		
		for(Event event: eventList) {
			storage.insert(event);
		}
		EventIterator iter = storage.query("venda", 100, 500);
		
		iter.moveNext();
		try {
			System.out.println(iter.current().timestamp());
		}
		catch(IllegalStateException e) {
			System.out.println(e.getError());
		}
		
		try {
			iter.remove();
		}
		catch(IllegalStateException e) {
			System.out.println(e.getError());
		}
		
		storage.removeAll("venda");
		iter.remove();
		storage.showAll();
		
	}
}
