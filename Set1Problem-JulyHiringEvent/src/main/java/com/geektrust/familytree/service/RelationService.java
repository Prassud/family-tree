package com.geektrust.familytree.service;

import com.geektrust.familytree.manager.FamilyTreeManager;
import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.utils.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.geektrust.familytree.bean.RelationShipMapBean.*;
import static java.util.Objects.*;

public class RelationService {

	public void updateChildrenInfo(Member mother, Member father, Member child) {
		mother.addChildren(child.getName());
		father.addChildren(child.getName());
	}

	public void addSiblingsTo(Member newBornChild, List<Member> siblings) {
		siblings.forEach((eachChildren) -> {
			eachChildren.addSibling(newBornChild);
			newBornChild.addSibling(eachChildren);
		});
	}

	public void findRelatives(Iterator<RelationShip> relationShipIterator,
	                          List<Member> familyMembers,
	                          List<String> resultantRelationNames) {

		if (!relationShipIterator.hasNext()) {
			resultantRelationNames.addAll(familyMembers.stream().map(Member::getName).collect(Collectors.toList()));
			return;
		}

		RelationShip relationShip = relationShipIterator.next();

		familyMembers.forEach((eachFamilyMember) -> {
			List<String> relationNames = eachFamilyMember.getRelation(relationShip.getRelationType());

			if (CollectionUtils.isEmpty(relationNames)) {
				return;
			}

			Stream<Member> relationStream = FamilyTreeManager.getInstance()
					.getMembersByName(relationNames);
			if (nonNull(relationShip.getGender())) {
				relationStream = relationStream.filter(getGenderFilter(relationShip));
			}

			List<Member> relations = relationStream.collect(Collectors.toList());
			findRelatives(relationShipIterator, relations, resultantRelationNames);
		});
	}

	private Predicate<Member> getGenderFilter(RelationShip relationShip) {
		return (eachMember) -> eachMember.getGender() == relationShip.getGender();
	}
}
