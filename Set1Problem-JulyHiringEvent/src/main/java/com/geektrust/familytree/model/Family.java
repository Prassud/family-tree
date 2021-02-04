package com.geektrust.familytree.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Family {
	private Map<String, Member> members;

	@JsonCreator
	public Family(@JsonProperty(value = "members") Map<String, Member> members) {
		this.members = members;
	}

	public Member getMember(String name) {
		return members.get(name);
	}

	public void addMember(String name, Member child) {
		this.members.put(name, child);
	}
}

