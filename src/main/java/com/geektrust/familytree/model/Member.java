package com.geektrust.familytree.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.geektrust.familytree.utils.CollectionUtils;

import java.util.*;

public class Member {

	private String name;

	private Gender gender;

	private Map<Relation.Type, List<String>> relations;

	@JsonCreator
	public Member(@JsonProperty(value = "name") String name,
	              @JsonProperty(value = "gender") Gender gender,
	              @JsonProperty(value = "relations") Map<Relation.Type, List<String>> relations) {
		this.gender = gender;
		this.name = name;
		this.relations = relations;
	}


	public Member(String name, Gender gender) {
		this.gender = gender;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Gender getGender() {
		return gender;
	}

	private Map<Relation.Type, List<String>> getRelations() {
		relations = Optional.ofNullable(relations).orElse(new HashMap<>());
		return relations;
	}

	public String getSpouse() {
		List<String> relations = getRelation(Relation.Type.SPOUSE);
		return CollectionUtils.isEmpty(relations) ? null : relations.get(0);
	}

	public List<String> getChildren() {
		return getRelation(Relation.Type.CHILDREN);
	}

	public void addParents(Member mother, Member father) {
		List<String> parentNames = new ArrayList<>();
		parentNames.add(mother.getName());
		parentNames.add(father.getName());

		getRelations().put(Relation.Type.PARENT, parentNames);
	}

	public void addChildren(String newBornChildName) {
		List<String> children = getRelations().getOrDefault(Relation.Type.CHILDREN, new ArrayList<>());
		children.add(newBornChildName);
		getRelations().putIfAbsent(Relation.Type.CHILDREN, children);
	}

	public void addSibling(Member sibling) {
		List<String> siblings = getRelations().getOrDefault(Relation.Type.SIBLING, new ArrayList<>());
		siblings.add(sibling.getName());
		getRelations().putIfAbsent(Relation.Type.SIBLING, siblings);
	}

	public List<String> getRelation(Relation.Type relationType) {
		return getRelations().get(relationType);
	}

	public boolean isMother() {
		return gender == Gender.FEMALE && Objects.nonNull(getSpouse());
	}

	public enum Gender {
		MALE, FEMALE
	}
}
