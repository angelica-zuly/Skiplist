
import java.util.Collection;
import java.util.Iterator;

public class Main {

	public static <T> void main(String[] args) {

		//create SkipListSet
		SkipListSet<Character> sls = new SkipListSet<Character>();
		SkipListSet<Character> sls2 = new SkipListSet<Character>();
		
		//adding values to list:			
		sls.add('o');
		sls.add('g');	
		sls.add('a');
		
		//adding values to list:			
		sls2.add('o');
		sls2.add('h');	
		sls2.add('a');


		System.out.println("Equal?: " + sls.equals(sls2));
		System.out.println("Set HashCode: " + sls.hashCode());
				
		//TESING: remove method
		sls.remove('m');		
		sls.add('p');

		System.out.println(" ");
		System.out.println("ADD/SIZE OF LIST: " + sls.size());
		System.out.println("FIRST ITEM: " + sls.first());
		System.out.println("LAST ITEM: " + sls.last());
		
		//Putting SkipListSet in an array
		Object[] array = sls.toArray();	

		//Displaying array
		for(Object o: array){  
			System.out.println("> " + o + ": " + o.hashCode());  	
		}
		
		//System.out.println("reBalanced:");
		//sls.reBalance();

		//Putting array into another array
		T list[] = (T[]) sls.toArray(array);

		//Displaying array
		for (T number : list) {
			System.out.println("> " + number);
		}


		System.out.println(" ");
		System.out.println("******************************");

		//TESTING: Creating a Collection
		Collection<Character> c = new  SkipListSet<Character>();
		c.add('l');
		c.add('b');
		c.add('w');
		//TESTING: Checking if items in collection are in the SkipListSet:
		if(sls.containsAll(c)) {
			System.out.println("SkipList contains all items in collection");
		}
		else {
			System.out.println("SkipList did not contain all items in collection");
		}

		System.out.println(" ");
		System.out.println("******************************");
		System.out.println(" ");
		System.out.println("******************************");	
		
		System.out.println("TESTING: Adding the collection items to SkipListSet");
		sls.addAll(c);
		System.out.println("TESTING: Printing new list (using Iterator)");	    
		Iterator<Character> itr = sls.iterator();	
		while(itr.hasNext()) {
			System.out.println("> " + itr.next());   	
		}

		System.out.println(" ");
		System.out.println("******************************");		

		System.out.println("TESTING: Remove everything in SkipList EXCEPT for collection items");
		sls.retainAll(c);
		itr = sls.iterator(); //reseting for testing
		while(itr.hasNext()) {
			System.out.println("> " + itr.next());   	
		}

		System.out.println(" ");
		System.out.println("******************************");

		System.out.println("TESTING: Removing collection items from SkipList");
		sls.removeAll(c);
		itr = sls.iterator(); //reseting for testing
		while(itr.hasNext()) {
			System.out.println("> " + itr.next());   	
		}	   
		if(itr.hasNext() == false) {
			System.out.println("empty!");
		}


	}
}
