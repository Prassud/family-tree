package com.geektrust.familytree.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;

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

    public void getGenerationCount(String name, GenerationLevel level) {
        Member member = members.get(name);
        Optional<String> fatherName = member.getRelation(Relation.Type.PARENT).stream()
                .filter(eachMember ->
                        Member.Gender.MALE.equals(members.get(eachMember).getGender()))
                .findFirst();
        if (fatherName.isPresent()) {
            level.increment();
            getGenerationCount(fatherName.get(), level);
        }
    }

    public static class GenerationLevel {
        private int val;

        public void increment() {
            this.val++;
        }

        public int getVal(){
            return val;
        }

    }
}

