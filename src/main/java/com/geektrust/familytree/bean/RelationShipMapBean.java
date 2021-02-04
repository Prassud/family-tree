package com.geektrust.familytree.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.model.Relation;

import java.util.List;
import java.util.Map;

public class RelationShipMapBean {
	private Map<String, List<RelationShip>> relationshipMap;

	private Map<String, List<List<RelationShip>>> multiRelations;

	@JsonCreator
	private RelationShipMapBean(@JsonProperty(value = "relationshipMap") Map<String, List<RelationShip>> relationshipMap,
	                            @JsonProperty(value = "multiRelations") Map<String, List<List<RelationShip>>> multiRelations) {
		this.relationshipMap = relationshipMap;
		this.multiRelations = multiRelations;
	}

	public List<RelationShip> getRelationShips(String relation) {
		return relationshipMap.get(relation);
	}

	public List<List<RelationShip>> getMultiRelation(String relation) {
		return multiRelations.get(relation);
	}

	public static class RelationShip {

		private Member.Gender gender;

		private Relation.Type relationType;

		@JsonCreator
		public RelationShip(@JsonProperty(value = "relationType") Relation.Type relationType, @JsonProperty(value = "gender") Member.Gender gender) {
			this.gender = gender;
			this.relationType = relationType;
		}

		public Member.Gender getGender() {
			return gender;
		}

		public Relation.Type getRelationType() {
			return relationType;
		}
	}
}
