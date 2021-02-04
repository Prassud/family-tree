package com.geektrust.familytree.utils;

import com.geektrust.familytree.exception.FamilyTreeException;
import com.geektrust.familytree.model.Family;
import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.model.Relation;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FamilyTreeBuilderTest {

	@Test
	public void shouldBuildFamilyTreeBasedOnInputFileTemplate() {
		Family family = FamilyTreeBuilder.build("shah_family_tree.json");

		Member shah = family.getMember("King Shah");
		List<String> relations = shah.getRelation(Relation.Type.SPOUSE);

		assertEquals("King Shah", shah.getName());
		assertEquals(Member.Gender.MALE, shah.getGender());
		assertEquals("Queen Ange", relations.get(0));

		Member queenAnge = family.getMember("Queen Ange");
		relations = queenAnge.getRelation(Relation.Type.SPOUSE);
		assertEquals("Queen Ange", queenAnge.getName());
		assertEquals(Member.Gender.FEMALE, queenAnge.getGender());
		assertEquals("King Shah", relations.get(0));
	}

	@Test(expected = FamilyTreeException.class)
	public void shouldThrowShahFamilyExceptionIfFileLoadingIsFailed() {
		FamilyTreeBuilder.build("ddf");
	}
}