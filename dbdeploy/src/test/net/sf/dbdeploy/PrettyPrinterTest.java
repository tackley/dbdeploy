package net.sf.dbdeploy;

import java.util.ArrayList;
import java.util.Arrays;

import net.sf.dbdeploy.scripts.ChangeScript;

import junit.framework.TestCase;

public class PrettyPrinterTest extends TestCase {
	PrettyPrinter prettyPrinter;
	
	@Override
	protected void setUp() throws Exception {
		prettyPrinter = new PrettyPrinter();
	}
	
	public void testShouldDisplayNonRangedNumbersAsSeperateEntities() throws Exception {
		assertEquals("1, 3, 5", prettyPrinter.format(Arrays.asList(1, 3, 5)));
	}
	
	public void testShouldDisplayARangeAsSuch() throws Exception {
		assertEquals("1..5", prettyPrinter.format(Arrays.asList(1, 2, 3, 4, 5)));
	}
	
	public void testRangesOfTwoAreNotDisplayedAsARange() throws Exception {
		assertEquals("1, 2", prettyPrinter.format(Arrays.asList(1, 2)));
	}
	public void testShouldReturnNoneWithAnEmptyList() throws Exception {
		assertEquals("(none)", prettyPrinter.format(new ArrayList<Integer>()));
	}
	
	public void testCanDealWithMixtureOfRangesAndNonRanges() throws Exception {
		assertEquals("1, 2, 4, 7..10, 12", prettyPrinter.format(Arrays.asList(1, 2, 4, 7, 8, 9, 10, 12)));
	}
	
	public void testCanFormatAChangeScriptList() throws Exception {
		ChangeScript change1 = new ChangeScript(1);
		ChangeScript change3 = new ChangeScript(3);
		assertEquals("1, 3", prettyPrinter.formatChangeScriptList(Arrays.asList(change1, change3)));
	}
}
