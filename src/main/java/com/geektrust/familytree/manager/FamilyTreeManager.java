package com.geektrust.familytree.manager;

import com.geektrust.familytree.model.Family;
import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.utils.FamilyTreeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FamilyTreeManager {

	public Family getFamily() {
		return family;
	}

	private Family family;

	private static FamilyTreeManager instance;

	private FamilyTreeManager(Family family) {
		this.family = family;
	}

	public static void initialize() {
		Family family = FamilyTreeBuilder.build("shah_family_tree.json");
		instance = new FamilyTreeManager(family);
	}

	public static FamilyTreeManager getInstance() {
		return instance;
	}

	public Stream<Member> getMembersByName(List<String> relation) {
		relation = Optional.ofNullable(relation).orElse(new ArrayList<>());
		return relation.stream().map((eachRelation) -> family.getMember(eachRelation));
	}

	public Member getMember(String name) {
		return family.getMember(name);
	}

	public void addMember(String name, Member child) {
		this.family.addMember(name, child);
	}
}

