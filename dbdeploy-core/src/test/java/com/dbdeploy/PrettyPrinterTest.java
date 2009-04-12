package com.dbdeploy;

import com.dbdeploy.scripts.ChangeScript;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import com.dbdeploy.PrettyPrinter;

public class PrettyPrinterTest {
	PrettyPrinter prettyPrinter = new PrettyPrinter();

	@Test
	public void shouldDisplayNonRangedNumbersAsSeperateEntities() throws Exception {
		assertEquals("1, 3, 5", prettyPrinter.format(Arrays.asList(1, 3, 5)));
	}

	@Test
	public void shouldDisplayARangeAsSuch() throws Exception {
		assertEquals("1..5", prettyPrinter.format(Arrays.asList(1, 2, 3, 4, 5)));
	}

	@Test
	public void rangesOfTwoAreNotDisplayedAsARange() throws Exception {
		assertEquals("1, 2", prettyPrinter.format(Arrays.asList(1, 2)));
	}

	@Test
	public void shouldReturnNoneWithAnEmptyList() throws Exception {
		assertEquals("(none)", prettyPrinter.format(new ArrayList<Integer>()));
	}

	@Test
	public void canDealWithMixtureOfRangesAndNonRanges() throws Exception {
		assertEquals("1, 2, 4, 7..10, 12", prettyPrinter.format(Arrays.asList(1, 2, 4, 7, 8, 9, 10, 12)));
	}

	@Test
	public void canFormatAChangeScriptList() throws Exception {
		ChangeScript change1 = new ChangeScript(1);
		ChangeScript change3 = new ChangeScript(3);
		assertEquals("1, 3", prettyPrinter.formatChangeScriptList(Arrays.asList(change1, change3)));
	}
}
