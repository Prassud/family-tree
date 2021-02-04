package com.geektrust.familytree.utils;

import com.geektrust.familytree.exception.FamilyTreeException;
import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.model.Relation;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.geektrust.familytree.bean.RelationShipMapBean.RelationShip;
import static com.geektrust.familytree.utils.RelationShipManager.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RelationShipManagerTest {

	@Test
	public void shouldInitializeTheRelationShipMapperFromRelationShipConfig() throws IOException {
		getInstance().load("relationship_mapper.json");

		List<RelationShip> relationShips = getInstance().getRelationShipChain("Paternal-Uncle");
		assertEquals(Member.Gender.MALE, relationShips.get(0).getGender());
		assertEquals(Relation.Type.PARENT, relationShips.get(0).getRelationType());

		RelationShip nextChain = relationShips.get(1);
		assertEquals(Member.Gender.MALE, nextChain.getGender());
		assertEquals(Relation.Type.SIBLING, nextChain.getRelationType());
	}

	@Test(expected = FamilyTreeException.class)
	public void shouldThrowShahFamilyExceptionIfFileLoadingIsFailed() {
		getInstance().load("relationship_mappe1r.json");
	}

	@Test
	public void shouldReturnNullWhenRelationShipDoesNotExist() {
		getInstance().load("relationship_mapper.json");

		List<RelationShip> relationShips = getInstance().getRelationShipChain("Paternal-Uncl11e");
		assertNull(relationShips);
	}
}