package com.geektrust.familytree;

import com.geektrust.familytree.model.Family;
import com.geektrust.familytree.utils.FamilyTreeBuilder;
import com.geektrust.familytree.utils.RelationShipManager;

import static com.geektrust.familytree.utils.RelationShipManager.getInstance;

public class BaseTest {

	protected RelationShipManager relationShipManager;

	protected Family family;

	protected void setUp() throws Exception {
		getInstance().load("relationship_mapper.json");

		FamilyTreeBuilder builder = new FamilyTreeBuilder();
		family = builder.build("shah_family_tree.json");
	}
}
