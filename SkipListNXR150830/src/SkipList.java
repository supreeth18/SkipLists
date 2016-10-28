import java.lang.Comparable;
import java.util.Iterator;
import java.util.Random;

public class SkipList<T extends Comparable<? super T>> {

	/*
	 * 
	 * All the Functions which are implemented here have followed the Exact
	 * similar approach of the Lecture 13 Class Notes PseudoCode -> SkipLists
	 */
	int skipListPeakSize;
	int limit;
	// pointer to first node - head
	SkipListNode<T> firstNode;
	int skipListSize;
	// pointer to last node - Tail
	SkipListNode<T> lastNode;

	SkipList(int skipSize, T skipListFirstNode, T skipListLastNode) {
		skipListSize = 0;
		skipListPeakSize = (int) Math
				.ceil(Math.log10(skipSize) / Math.log10(2));
		limit = skipSize;
		firstNode = new SkipListNode<>(skipListFirstNode);
		lastNode = new SkipListNode<>(skipListLastNode);
		// for i->0 to maxlevel
		for (int i = 0; i <= skipListPeakSize; i++) {
			firstNode.nextPointer[i] = lastNode;
			lastNode.nextPointer[i] = null;
		}
		lastNode.previousPointer = firstNode;
	}

	@SuppressWarnings("hiding")
	class SkipListNode<T> {

		SkipListNode<T>[] nextPointer;
		SkipListNode<T> previousPointer;
		T data;

		@SuppressWarnings("unchecked")
		SkipListNode(T x) {
			nextPointer = new SkipListNode[skipListPeakSize + 1];
			previousPointer = null;
			data = x;
		}

		@SuppressWarnings("unchecked")
		SkipListNode(T x, int howManyLevels) {
			nextPointer = new SkipListNode[howManyLevels + 1];
			previousPointer = null;
			data = x;
			for (int i = 0; i < howManyLevels; i++) {
				nextPointer[i] = null;
			}
		}
	}

	/**
	 * Helper function to locate x.
	 * 
	 */
	@SuppressWarnings("unchecked")
	SkipListNode<T>[] find(T x) {
		SkipListNode<T>[] previous = new SkipListNode[skipListPeakSize + 1];
		SkipListNode<T> currentNode;
		// here p points to header
		currentNode = firstNode;
		// for i<-maxlevel down to zero travelling along level i
		for (int i = skipListPeakSize; i >= 0; i--) {
			while (currentNode.nextPointer[i].data.compareTo(x) < 0) {
				currentNode = currentNode.nextPointer[i];
			}
			previous[i] = currentNode;
		}
		// Here previous is ->The nodes where algorithm went down one level.
		return previous;
	}

	/*
	 * If X exists replace it else return true if new node is added
	 */
	boolean add(T x) {
		SkipListNode<T>[] previous;
		previous = find(x);
		// if the element already exists don't add.
		if (previous[0].nextPointer[0].data.compareTo(x) == 0) {
			previous[0].nextPointer[0].data = x;
			return false;
		}

		else {

			int level = probabilityOfFlippingCoin(skipListPeakSize);
			// new node is created and added to skiplists
			SkipListNode<T> newNode = new SkipListNode<>(x, level);
			for (int i = 0; i <= level; i++) {
				newNode.nextPointer[i] = previous[i].nextPointer[i];
				previous[i].nextPointer[i] = newNode;
			}
			newNode.previousPointer = newNode.nextPointer[0].previousPointer;
			newNode.nextPointer[0].previousPointer = newNode;
			skipListSize++;
			// if the size of the skip list exceeds the limit then call rebuild
			// function i,e basically when we are not able to maintain
			// the balance equally between all the levels we may approach o(n)
			// time so in order to maintain proper balance
			// between all the levels we will call rebuild function.
			if (skipListSize >= limit) {
				rebuild();
			}
			return true;
		}
	}

	boolean remove(T x) {
		// return false since x is not found
		if (skipListSize == 0)
			return false;
		SkipListNode<T>[] previousPointer;
		SkipListNode<T> elementToBeCompared;
		previousPointer = find(x);
		elementToBeCompared = previousPointer[0].nextPointer[0];
		// Here if the element does not exist in the skip list return false
		if (elementToBeCompared.data.compareTo(x) != 0)
			return false;

		else {
			// for i->0 to maxlevel do
			for (int i = 0; i <= skipListPeakSize; i++) {
				if (previousPointer[i].nextPointer[i] == elementToBeCompared)
					previousPointer[i].nextPointer[i] = elementToBeCompared.nextPointer[i];
				else
					break;
			}
			// if the element x that you are trying to remove is present then
			// remove that element and decrease the skiplist size
			// finally return true.
			elementToBeCompared.nextPointer[0].previousPointer = previousPointer[0];
			skipListSize--;
			return true;
		}
	}

	/**
	 * Here we will flip a coin say if we encounter head we will go to Next
	 * Level else we will stay in that base level itself probability of choosing
	 * a level i = 1/2 probability {choosing level i-1}
	 */
	int probabilityOfFlippingCoin(int level) {
		int currentLevel = 0;
		Random flipCoin = new Random();
		while (currentLevel < skipListPeakSize) {
			// The nextBoolean() method is used to get the next pseudorandom,
			// uniformly distributed boolean value from this random number
			// generator's sequence.
			if (flipCoin.nextBoolean())
				currentLevel++;
			else
				break;
		}
		return currentLevel;
	}

	/*
	 * Here this function will return Least element that is >= x, or null if no
	 * such element
	 */
	T ceiling(T x) {
		if (skipListSize == 0)
			return null;
		SkipListNode<T>[] previousPointer;
		previousPointer = find(x);
		if (previousPointer[0].nextPointer[0].data.compareTo(x) == 0)
			return x;
		else {
			if (previousPointer[0].nextPointer[0] != lastNode)
				return previousPointer[0].nextPointer[0].data;
			else
				return null;
		}
	}

	/*
	 * Here this function will check whether x in the list?
	 */

	boolean contains(T x) {
		if (skipListSize == 0)
			return false;
		SkipListNode<T>[] previousPointer;
		previousPointer = find(x);
		return previousPointer[0].nextPointer[0].data.compareTo(x) == 0;
	}

	/*
	 * Here this function will Return the element at index n in the list
	 */
	T findIndex(int n) {
		// If the element is not there in the list return null else return the
		// index
		if (n >= skipListSize || n < 0)
			return null;
		SkipListNode<T> pointer = firstNode.nextPointer[0];
		for (int i = 0; i < n; i++)
			pointer = pointer.nextPointer[0];
		return pointer.data;
	}

	/*
	 * Returns the first element of the list
	 */
	T first() {
		return firstNode.nextPointer[0] != lastNode ? firstNode.nextPointer[0].data
				: null;
	}

	/*
	 * Greatest element that is <= x, or null if no such element As usual first
	 * get the index of where the element is located using find(x) and then
	 * search in the skip list ie Greatest element that is <= x
	 */
	T floor(T x) {
		if (skipListSize == 0)
			return null;
		SkipListNode<T>[] previousPointer;
		previousPointer = find(x);
		if (previousPointer[0].nextPointer[0].data.compareTo(x) == 0)
			return x;
		else {

			if (previousPointer[0] == firstNode)
				return null;
			else
				return previousPointer[0].data;
		}
	}

	/*
	 * Is the list empty? or Not ?
	 */
	boolean isEmpty() {
		if (skipListSize == 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Iterator for SkipList
	 */
	Iterator<T> iterator() {
		return new SLIterator();
	}

	public class SLIterator implements Iterator<T> {
		private SkipListNode<T> nodeBeingProcessed;

		// Points to the first Node in skip List
		SLIterator() {
			nodeBeingProcessed = firstNode;
		}

		@Override
		public boolean hasNext() {
			return nodeBeingProcessed.nextPointer[0] != lastNode;
		}

		@Override
		public T next() {
			T data = nodeBeingProcessed.nextPointer[0].data;
			nodeBeingProcessed = nodeBeingProcessed.nextPointer[0];
			return data;
		}
	}

	/*
	 * Returns the last element of the list
	 */
	T last() {
		return lastNode.previousPointer != firstNode ? lastNode.previousPointer.data
				: null;
	}

	/*
	 * Rebuilding to a Perfect SkipList Here the mail goal is to maintain the
	 * balance between all the levels we do that by flipping a coin say when we
	 * get head we add the element and extend the base lane to express lane then
	 * if we get head again we go level up else we keep on adding to base level
	 * itself. so by flipping a coin if we are able to maintain perfect balance
	 * say half elements in level 1 and quarter elements in level 2 and so on
	 * proper balance will be maintained between all the levels so we can
	 * achieve the wanted log(n) time.
	 */
	@SuppressWarnings("unchecked")
	void rebuild() {

		skipListPeakSize = skipListPeakSize * 2;
		limit = (int) Math.pow(2, skipListPeakSize);

		int currentLevel = 0;
		SkipListNode<T>[] previousPointer = new SkipListNode[skipListPeakSize + 1];
		SkipListNode<T> firstNodeAfterAdding = firstNode;
		SkipListNode<T> modifiedLastNode = lastNode;
		SkipListNode<T> pointerToTheNextNode = firstNodeAfterAdding.nextPointer[0];

		firstNode = new SkipListNode<>(firstNodeAfterAdding.data,
				skipListPeakSize);
		lastNode = new SkipListNode<>(modifiedLastNode.data, skipListPeakSize);

		for (int i = 0; i <= skipListPeakSize; i++) {
			firstNode.nextPointer[i] = lastNode;
			lastNode.nextPointer[i] = null;
			previousPointer[i] = firstNode;
		}
		lastNode.previousPointer = firstNode;

		for (int i = 0; i < skipListSize; i++) {
			SkipListNode<T> numberOfElements = new SkipListNode<>(
					pointerToTheNextNode.data, currentLevel);
			for (int j = 0; j <= currentLevel; j++) {
				numberOfElements.nextPointer[j] = previousPointer[j].nextPointer[j];
				previousPointer[j].nextPointer[j] = numberOfElements;
				previousPointer[j] = numberOfElements;
			}

			/*
			 * Here the pointers which are in each level will be pointing
			 * exactly 2 to the power level from it.
			 */
			currentLevel = (currentLevel + 1) % skipListPeakSize;
			numberOfElements.nextPointer[0].previousPointer = numberOfElements;
			pointerToTheNextNode = pointerToTheNextNode.nextPointer[0];
		}
	}

	int size() {
		return skipListSize;
	}

}
