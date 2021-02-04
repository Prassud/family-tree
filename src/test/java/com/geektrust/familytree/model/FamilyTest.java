package com.geektrust.familytree.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FamilyTest {

    @Test
    public void shouldReturnGenerationCountForMember() {
        Family family = setUpBasicFamily();
        Family.GenerationLevel level = new Family.GenerationLevel();
        family.getGenerationCount("gen1", level);

        assertEquals(1, level.getVal());
    }


    @Test
    public void shouldReturnGenerationCountForMultiLevelMember() {
        Family family = setUpBasicFamily();
        Family.GenerationLevel level = new Family.GenerationLevel();
        family.getGenerationCount("gen2", level);

        assertEquals(2, level.getVal());
    }

    @Test
    public void shouldReturnZeroAsGenerationCountForRootMember() {
        Family family = setUpBasicFamily();
        Family.GenerationLevel level = new Family.GenerationLevel();
        family.getGenerationCount("gen0", level);

        assertEquals(0, level.getVal());
    }

    private Family setUpBasicFamily() {
        Map<String, Member> members = new HashMap<>();
        Member rootGenMember = new Member("gen0", Member.Gender.MALE);

        Map<Relation.Type, List<String>> relationshipMap = new HashMap<>();
        ArrayList<String> relations = new ArrayList<>();
        relations.add("gen0");
        relationshipMap.put(Relation.Type.PARENT, relations);
        members.put("gen0", rootGenMember);
        Member firstGenMember = new Member("gen1", Member.Gender.MALE, relationshipMap);

        relationshipMap = new HashMap<>();
        relations = new ArrayList<>();
        relations.add("gen1");
        relationshipMap.put(Relation.Type.PARENT, relations);
        members.put("gen0", rootGenMember);
        Member secondGenMember = new Member("gen2", Member.Gender.MALE, relationshipMap);

        members.put("gen1", firstGenMember);
        members.put("gen2", secondGenMember);
        members.put("gen0", rootGenMember);

        return new Family(members);
    }
}