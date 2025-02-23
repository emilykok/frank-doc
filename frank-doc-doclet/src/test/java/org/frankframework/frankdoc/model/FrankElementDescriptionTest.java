package org.frankframework.frankdoc.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FrankElementDescriptionTest {
	@Test
	public void whenDescriptionMissesDotSpaceThenDescriptionHeaderIsSame() {
		String description = "This is a sentence.";
		assertEquals("This is a sentence.", FrankElement.calculateDescriptionHeader(description));
	}

	@Test
	public void whenDescriptionMissesDotThenNoExtraDotAdded() {
		String description = "This is a sentence";
		assertEquals("This is a sentence", FrankElement.calculateDescriptionHeader(description));		
	}

	@Test
	public void whenDescriptionHasDotSpaceThenDescriptionHeaderFirstSentenceWithDot() {
		String description = "This is a sentence. This is the second sentence";
		assertEquals("This is a sentence.", FrankElement.calculateDescriptionHeader(description));		
	}

	@Test
	public void whenDescriptionHasDotNewlineThenDescriptionHeaderFirstSentenceWithDot() {
		String description = "This is a sentence.\nThis is the second sentence";
		assertEquals("This is a sentence.", FrankElement.calculateDescriptionHeader(description));		
	}
}
