#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nair.example.*;

public class TestLinkedLab {
	
	private static final int[] array1 = {1,2,2,3,4,1,6,3,4,5};
	private static final int[] array2 = {5,3,2,4,2,5,3,1};
	private static final int[] array3 = {2};
	private static final int[] array4 = {2,2};
	private static final int[] array5 = {3};
	
	private static final int testValue = 2;
	
	private RyanUnorderedLinkedList list1;
	private RyanUnorderedLinkedList list2;
	private RyanUnorderedLinkedList list3;
	private RyanUnorderedLinkedList list4;
	private RyanUnorderedLinkedList list5;
	
	
	@Before
	public void setUpLists() {
		// this will run before each test
		list1 = createLinkedList(array1);
		list2 = createLinkedList(array2);
		list3 = createLinkedList(array3);
		list4 = createLinkedList(array4);
		list5 = createLinkedList(array5);
	}
	
	private RyanUnorderedLinkedList createLinkedList(int[] vals) {
		
		RyanUnorderedLinkedList ul = new RyanUnorderedLinkedList();
		for(int z : vals) {
			DataElement del = new IntElement(z);
			ul.insertLast(del);
		}
		
		return ul;
	}
	
	@Test
	public void testDeleteSmallest() {
		
		// Failures here will NOT cause the test to fail
		// Remember this if you're using Maven to build
		// and relying on the test phase!
		removeSmallest(list1);
		removeSmallest(list2);
		removeSmallest(list3);
		removeSmallest(list4);
		removeSmallest(list5);
	}
	
	@Test
	public void testDeleteAll() {
		removeAll(list1, 2);
		removeAll(list2, 2);
		removeAll(list3, 2);
		removeAll(list4, 2);
		removeAll(list5, 2);
		
		DataElement testElement =  new IntElement(testValue);
		
		// if wasn't removed from even one of these, the test will fail!
		Assert.assertFalse( 
				list1.search(testElement) &&
				list2.search(testElement) &&
				list3.search(testElement) &&
				list4.search(testElement) &&
				list5.search(testElement)
				);
	}
	
	private void removeSmallest(RyanUnorderedLinkedList ul) {
		
		System.out.println("${symbol_escape}n${symbol_escape}nBEFORE: ");
		ul.print();
		ul.deleteSmallest();
		System.out.println("${symbol_escape}nAfter deleteSmallest:${symbol_escape}n");
		ul.print();
	}
	
	private void removeAll(RyanUnorderedLinkedList ul, int value) {
		
		System.out.println("${symbol_escape}n${symbol_escape}nBEFORE: ");
		ul.print();
		ul.deleteAll(new IntElement(value));
		System.out.println("${symbol_escape}nAfter deleteAll("+value+"):${symbol_escape}n");
		ul.print();
	}
	
}
