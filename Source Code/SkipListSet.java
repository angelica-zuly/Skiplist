
import java.util.SortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

	private SkipListSetItem header;
	private int totalHashCode = 0;
	private int setSize = 0;

	/**
	 * Default constructor that creates an empty SkipListSetItem.
	 */
	public SkipListSet() {
		header = new SkipListSetItem(null, 0);
	}

	/**
	 * Constructor that creates a new SkipListSetItem with the specified data.
	 * @param value the node data
	 */
	public SkipListSet(T data) {
		header = new SkipListSetItem(data, 0);
	}

	/**
	 * internal CLASS creating SkipListSetItems:
	 */
	private class SkipListSetItem{		
		public T data;
		public int height;
		public SkipListSetItem rightPtr = null;
		public SkipListSetItem downPtr = null;

		/**
		 * Constructor that creates a new SkipListSetItem with the specified data and height.
		 * @param data the data
		 * @param height the height
		 */
		public SkipListSetItem(T data, int height) {
			this.data = data;
			this.height = height;		
		}		

	}

	/**
	 * internal CLASS implementing Iterator:	 
	 */
	private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {

		private SkipListSetItem setItem = header;

		@Override
		public boolean hasNext() {			
			boolean atBottom = false;				
			while (!atBottom) {
				//loop to bottom level:
				if (setItem.height == 0) {
					atBottom = true;
				} else {
					setItem = setItem.downPtr;
				}
			}
			//checking for a next element
			if (setItem.rightPtr == null) {
				return false;

			} else{
				return true;
			}			
		}

		@Override	
		public T next() {
			T element = (T)setItem.rightPtr.data;
			setItem = setItem.rightPtr;	
			return element;
		}

		public void remove() {
			SkipListSetItem temp = setItem;
			setItem = setItem.rightPtr;
			SkipListSet.this.remove((Object)temp.data);
		}

	}

	@Override
	public Iterator<T> iterator() {	
		return new SkipListSetIterator<T>();
	}

	
	//***LOCAL METHODS***//
	
	/**
	 * Method to change height of each item in SkipListSet.
	 */
	public void reBalance() {
		Object[] array = toArray();	
		// Clearing the SkipListSet
		clear();
		for(Object o: array){  
			// Adding each element back to the SkipListSet
			add((T) o);
		}		
	}

	/**
	 * Method to walk through list - insert
	 * @param element the list element
	 * @param newNodeHeight the randomly generated height
	 */
	private void iterateToInsert(T element, int newNodeHeight) {

		SkipListSetItem current = header; //make a copy of header
		boolean found = false;

		while(!found) {		
			if(current.rightPtr == null  && current.height != newNodeHeight) {
				//if the right of current is pointing to null, and we ARE NOT at the 
				//height we are looking for, then we go down a level
				current = current.downPtr;

			} else if(current.rightPtr == null  && current.height == newNodeHeight) {
				//if the right of current is pointing to null, and we ARE at the height we
				//are looking for, then we can manipulate the end of the SKipListSet
				insertTail(element, newNodeHeight, current);
				found = true;

			} else if((element).compareTo(current.rightPtr.data) > 0) {
				//if the item we want to insert is GREATER than the right of current, then update current	
				current = current.rightPtr;

			} else if((element).compareTo(current.rightPtr.data) < 0  && (current.height == newNodeHeight)) {
				//if the item we want to insert is LESS than the right of current, and we ARE at the height we are 
				//looking for, then we can manipulate the middle of the SKipListSet	
				insertMiddle(element, newNodeHeight, current);
				found = true;

			} else if((element).compareTo(current.rightPtr.data) < 0  && (current.height != newNodeHeight)) {
				//if the item we want to insert is LESS than the right of current, and we ARE NOT at the height we are 
				//looking for, then we go down a level	
				current = current.downPtr;
			}			
		}
	}

	/**
	 * Inserts a new SkipListSetItem at the END of the SkipListSet (returns true after insertion)
	 * @param item the item to insert
	 * @param h the height
	 * @param current the pointer that walk to the item
	 * @return inserted
	 */
	private boolean insertTail(T item, int h, SkipListSetItem current) {

		boolean insterted = false;
		SkipListSetItem temp = current; //making copy

		//Inserting newItem at height 'h' provided
		SkipListSetItem newItem = new SkipListSetItem(item,h);
		temp.rightPtr = newItem;
		newItem.rightPtr = null;

		//Check if insertion was at bottom level, if not, move down
		if (current.downPtr == null) {
			insterted = true;
		} else {
			current = current.downPtr;
		}

		//continues to insert at lower levels:
		while(!insterted) {	
			if(current == null) {
				//Reached bottom level
				insterted = true;

			} else {			
				if (current.data == null && current.rightPtr == null) {		
					//inserting after header (null) AND header's next IS null - insert	

					//creating a lower level of the new item
					SkipListSetItem temp2 = new SkipListSetItem(item,--h);
					temp = current;
					temp.rightPtr = temp2;
					temp2.rightPtr = null;
					newItem.downPtr = temp2;				
					newItem = temp2; //updating newItem
					current = current.downPtr;			

				} else if (current.data == null
						&& current.rightPtr != null
						&& (item).compareTo(current.rightPtr.data) > 0 ) {
					//moving current from header (null) to the right, if header's next IS NOT null and < item
					current = current.rightPtr;

				} else if(current.rightPtr == null && (item).compareTo(current.data) > 0) {
					//assuming we are not at header (null)...
					//if the item we want to insert is GREATER than current AND current's next IS null - insert	

					//creating a lower level of the new item
					SkipListSetItem temp2 = new SkipListSetItem(item,--h);
					temp = current;
					temp.rightPtr = temp2;
					temp2.rightPtr = null;
					newItem.downPtr = temp2;
					newItem = temp2;
					current = current.downPtr;

				} else if((item).compareTo((T) current.rightPtr.data) > 0 && current.rightPtr != null) {
					//if the item we want to insert is GREATER than current's right AND current's right is NOT null - update current	
					current = current.rightPtr;

				} else if((item).compareTo(current.rightPtr.data) < 0 && (current.rightPtr != null)) {
					//if the item we want to insert is LOWER than current's right AND current's next is NOT null - insert	

					//creating a lower level of the new item
					SkipListSetItem temp2 = new SkipListSetItem(item,--h);
					temp = current.rightPtr;
					current.rightPtr = temp2;
					temp2.rightPtr = temp;
					newItem.downPtr = temp2;	
					newItem = temp2;
					current = current.downPtr;
				}
			}			
		}//end while loop

		return insterted;
	}

	/**
	 * Inserts a new SkipListSetItem at the MIDDLE of the SkipListSet (returns true after insertion)
	 * @param item
	 * @param height
	 * @param current
	 * @return inserted
	 */
	private boolean insertMiddle(T item, int h, SkipListSetItem current) {
		boolean inserted = false;

		//Inserting newItem at height h provided
		SkipListSetItem temp = current.rightPtr;
		SkipListSetItem newItem = new SkipListSetItem(item, h);
		current.rightPtr = newItem;
		newItem.rightPtr = temp;

		if (current.height == 0) {
			// inserted at bottom level
			inserted = true;

		} else {
			current = current.downPtr;
		}

		while (!inserted) {			
			// Continue to insert at lower levels
			if(current.height == 0) {	
				//insert one more time and then break
				SkipListSetItem temp2 = new SkipListSetItem(item,0);

				if((item).compareTo(current.rightPtr.data) > 0) {
					//if the item we want to insert is GREATER than the right of current - update current
					current = current.rightPtr;
				}
				else {
					temp = current.rightPtr;
					current.rightPtr = temp2;					
					temp2.rightPtr = temp;
					newItem.downPtr = temp2;
					inserted = true;
				}				

			} else if(current.height > 0) {
				// height not at bottom level				

				if ((item).compareTo(current.rightPtr.data) > 0) {
					//if the item we want to insert is GREATER than the right of current - update current
					current = current.rightPtr;

				} else if((item).compareTo(current.rightPtr.data) < 0) {
					//if the item we want to insert is LESS than the right of current - insert

					//creating a lower level of the new item
					SkipListSetItem temp2 = new SkipListSetItem(item,--h);
					temp = current.rightPtr;
					current.rightPtr = temp2;					
					temp2.rightPtr = temp;
					newItem.downPtr = temp2;	
					newItem = temp2; //move down newItem	
					current = current.downPtr; //move current down

				} else if((item).compareTo(current.data) > 0) {
					//if the item we want to insert is GREATER then current - insert

					//creating a lower level of the new item
					SkipListSetItem temp2 = new SkipListSetItem(item,--h);
					temp = current.rightPtr;
					current.rightPtr = temp2;
					current = current.downPtr; //move current down
					temp2.rightPtr = temp;
					newItem.downPtr = temp2;	
					newItem = temp2; //move down newItem			
				}
			}			
		}
		return inserted;
	}

	/**
	 * Generates a randomly picked height for each newly created SkipListSetItem
	 * @return the height generated
	 */
	private int pickHeight() {	
		Random coin = new Random();
		int height = 0;

		//flip coin:
		while(coin.nextInt(2) != 0) {
			height++;			
		}
		return height;
	}

	/**
	 * Method to increase height of header.
	 * @param newNodeHeight the new node height
	 */
	private void grow(int newNodeHeight) {
		int ListHeight = header.height;
		for(int i = 0; i < (newNodeHeight - ListHeight) + 1; i++) {	
			//adding another null level to header
			SkipListSetItem newHeaderItem = new SkipListSetItem(null,(header.height) + 1);
			newHeaderItem.downPtr = header;
			header = newHeaderItem;	//update the new header pointer
		}
	}

	/**
	 * Method to decrease height of header.
	 */
	private void trim() {
		boolean done = false;
		SkipListSetItem current = header.downPtr;		
		while(!done) {
			if(isEmpty()) {
				break;
			}
			if(current.rightPtr == null) {
				// Drop header down a level
				header = header.downPtr;
				current = current.downPtr;
			}
			else {
				done = true;
			}
		}	
	}	

	/**
	 * Moves to bottom level of SkipListSet
	 * @return bottom lever of header
	 */
	private SkipListSetItem bottomLevel() {
		SkipListSetItem temp = header;
		boolean bottomLvl = false;
		//loop to bottom level:
		while(!bottomLvl) {
			if(temp.height == 0) {
				bottomLvl = true;
			}
			else {
				temp = temp.downPtr;
			}
		}		
		return temp;
	}

	
	//***METHODS FROM SORTEDSET***//

	@Override
	public boolean add(T e) {
		boolean added;
		//Generate a randomly picked height for new item
		int newNodeHeight = pickHeight();

		//checking if list contains item, if so return false
		if(contains(e)) {
			added = false;
		} 
		else {
			//checking if header needs to be updated
			if(newNodeHeight >= header.height) {
				grow(newNodeHeight);
			}
			//finding place to insert new item:
			iterateToInsert(e,newNodeHeight);
			added = true;
			totalHashCode += e.hashCode();
			setSize++;
		}
		return added;
	}

	@Override	
	public boolean addAll(Collection<? extends T> c) {
		boolean added = false;
		for(T element: c) {
			if(add(element)) {
				added = true;
			}
		}
		return added;
	}

	@Override
	public void clear() {
		header.height = 0;
		header.rightPtr = null;
		header.downPtr = null;
		totalHashCode = 0;
		setSize = 0;
	}

	@Override	
	public boolean contains(Object o) {
		boolean found = false;
		SkipListSetItem checker = header; 

		if(isEmpty()) {
			//Object not found in empty list
			return found;
		}
		
		while(!found) {				
			if(checker.rightPtr == null && checker.height != 0) {
				checker = checker.downPtr;
			}
			else if(checker.rightPtr == null && checker.height == 0) {
				break;
			}
			else if(((Comparable<T>) o).compareTo(checker.rightPtr.data) > 0) {
				//If the item we are looking for is GREATER than checker's right - move right
				checker = checker.rightPtr;
			}
			else if(((Comparable<T>) o).compareTo(checker.rightPtr.data) < 0 && checker.height != 0) {
				//If the item we are looking for is LESS than checker's right - move down
				checker = checker.downPtr;
			}
			else if(((Comparable<T>) o).compareTo(checker.rightPtr.data) == 0) {
				//If the item we are looking for is EQULAED to checker's right - return true
				found = true;
				break;
			}
			else if(checker.height == 0) {
				break;
			}
		}
		return found;
	}

	@Override	
	public boolean containsAll(Collection<?> c) {
		boolean found = true;
		for(Object o: c) {
			if(!contains(o)) {
				found = false;
			}
		}
		return found;
	}

	@Override
	//Checks if two sets are equaled
	public boolean equals(Object o) {
		boolean equaled = false;		
		if(!(o instanceof SkipListSet)) {
			return equaled;
		}
		Collection<?> c = (Collection<?>) o;
		if(c.size() == size() && containsAll(c)) {
			equaled = true;
		}
		return equaled;
	}

	@Override
	public int hashCode() {
		return totalHashCode;
	}

	@Override	
	public boolean isEmpty() {
		boolean empty = true;
		if(header.height > 0) {
			//not empty, if we are not at height 0
			empty = false;
		}
		return empty;
	}

	@Override
	public boolean remove(Object o) {	
		boolean removed = false;

		//checking if list contains item to be removed
		if(contains(o)) {			
			SkipListSetItem current = header; //make a copy of header				

			//if we are at the very top header level, drop down 
			if(current.data == null && current.rightPtr == null && current.height != 0) {
				current = current.downPtr;
			}

			while(!removed) {					
				if( current.rightPtr != null && ((Comparable<T>) o).compareTo(current.rightPtr.data) == 0) {
					//item we want to remove is EQUALED to the right of current
					current.rightPtr = current.rightPtr.rightPtr;
					if(current.height != 0) {
						current = current.downPtr;

					} else {
						//current's height is 0 - remove item
						removed = true;
						totalHashCode -= o.hashCode();
						setSize--;
					}

				} else if( current.rightPtr != null && ((Comparable<T>) o).compareTo(current.rightPtr.data) > 0) {
					//if the item we want to remove is GREATER than the right of current, then update current	
					current = current.rightPtr;

				} else if(current.rightPtr == null || ((Comparable<T>) o).compareTo(current.rightPtr.data) < 0) {
					//if the item we want to remove is LESS than the right of current, drop down
					current = current.downPtr;
				}

			}//end while loop

		} 	
		if(header.downPtr.rightPtr == null && isEmpty() == false) {
			// We need to trim the height of header after removing an item
			trim();
		}
		return removed;	
	}

	@Override	
	public boolean removeAll(Collection<?> c) {
		boolean removed = false;
		for(Object o: c) {
			if(contains(o)) {
				remove(o);
				removed = true;
			}
		}
		return removed;
	}

	@Override	
	public boolean retainAll(Collection<?> c) {
		//removes all elements not contained in specified collection.

		SkipListSetItem current = bottomLevel(); //go to bottom level
		current = current.rightPtr;	
		
		boolean done = false;
		while(current != null) {
			if(!c.contains(current.data)) {
				//if the collection does not contain the current data, remove it
				SkipListSetItem temp = current;
				current = current.rightPtr;
				remove(temp.data);
				done = true;
			} else {
				current = current.rightPtr;
			}		
		}
		return done;	
	}

	@Override
	public int size() {
		return setSize;
	}

	@Override	
	public Object[] toArray() {
		//Returns array containing all elements in set.		
		SkipListSetItem temp = bottomLevel();	
		temp = temp.rightPtr;

		Object array[] = new Object[size()];		
		int i = 0;
		while(temp != null) {		
			array[i] = temp.data;			
			if(temp.rightPtr != null) {
				temp = temp.rightPtr;
			}else {
				break;
			}
			i++;
		}
		return array;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		T[] b = a;
		for(int i=0;i<a.length;i++) {
			b[i] = a[i];
		}
		return b;
	}

	@Override
	//Returns the first (lowest) element currently in this set
	public T first() {
		SkipListSetItem temp = bottomLevel();
		if(isEmpty() == false && temp.rightPtr.data != null) {
			//Grab first item of the SkipList
			return temp.rightPtr.data;
		}
		else {
			//SkipList is empty - return null
			return null;
		}		
	}

	@Override
	//Returns the last (highest) element currently in this set.
	public T last() {
		
		if(isEmpty()) {
			return null;
		}
		//list is not empty, so loop to end of list
		SkipListSetItem temp = bottomLevel(); 
		boolean end = false;
		while(!end) {
			if(temp.rightPtr != null) {
				temp = temp.rightPtr;
			}
			else {
				end = true;
			}
		}
		return temp.data;
	}
	
	@Override
	public Comparator<? super T> comparator() {
		return null;
	}

	@Override	
	public SortedSet<T> headSet(T toElement) {
		throw new UnsupportedOperationException("Invaild operation");
	}

	@Override	
	public SortedSet<T> subSet(T fromElement, T toElement) {
		throw new UnsupportedOperationException("Invaild operation");
	}

	@Override	
	public SortedSet<T> tailSet(T fromElement) {
		throw new UnsupportedOperationException("Invaild operation");
	}

}
