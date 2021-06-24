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
		
		System.out.println("terminou de inserir tudo");
		
		storage.showAll();
		
		System.out.println("testando remocao");
		
		storage.removeAll("venda");
		
		storage.showAll();
		
	}
}
