package com.geektrust.familytree.service;

import com.geektrust.familytree.manager.FamilyTreeManager;
import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.model.Result;
import com.geektrust.familytree.utils.Constants;
import com.geektrust.familytree.utils.RelationShipManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.geektrust.familytree.bean.RelationShipMapBean.RelationShip;
import static com.geektrust.familytree.utils.CollectionUtils.isEmpty;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class FamilyTreeService {

    private RelationService relationService;

    private static final FamilyTreeManager FAMILY_TREE_MANAGER = FamilyTreeManager.getInstance();

    private static final RelationShipManager RELATION_SHIP_MANAGER = RelationShipManager.getInstance();

    public FamilyTreeService(RelationService relationService) {
        this.relationService = relationService;
    }

    public Result addChildren(String motherName, Member.Gender gender, String name) {
        Member mother = FAMILY_TREE_MANAGER.getMember(motherName);
        if (isNull(mother)) {
            return new Result(Constants.INVALID_PERSON, false);
        }
        if (!mother.isMother()) {
            return new Result(Constants.CHILD_ADDITION_FAILED, false);
        }

        Member father = FAMILY_TREE_MANAGER.getMember(mother.getSpouse());
        if (isNull(father)) {
            return new Result(Constants.INVALID_PERSON, false);
        }

        Member child = new Member(name, gender);
        child.addParents(mother, father);

        List<String> childrenNames = mother.getChildren();
        if (!isEmpty(childrenNames)) {
            relationService.addSiblingsTo(child, FAMILY_TREE_MANAGER
                    .getMembersByName(childrenNames).collect(Collectors.toList()));
        }

        relationService.updateChildrenInfo(mother, father, child);
        FAMILY_TREE_MANAGER.addMember(name, child);
        return new Result(Constants.CHILD_ADDITION_SUCCEEDED, true);
    }

    public Result getFamilyMember(String name, String relation) {
        Member familyMember = FAMILY_TREE_MANAGER.getMember(name);

        if (isNull(familyMember)) {
            return new Result(Constants.INVALID_PERSON, false);
        }

        List<RelationShip> relationShips =
                RELATION_SHIP_MANAGER.getRelationShipChain(relation);
        if (nonNull(relationShips)) {
            return resultForGetRelation(findRelation(familyMember, relationShips));
        }

        List<List<RelationShip>> multiLevelRelationShipChain =
                RELATION_SHIP_MANAGER.getMultiLevelRelationShipChain(relation);
        if (isNull(multiLevelRelationShipChain)) {
            return new Result(Constants.INVALID_PERSON, false);
        }

        return resultForGetRelation(multiLevelRelationShipChain.stream()
                .map(eachRelation -> findRelation(familyMember, eachRelation))
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }

    private List<String> findRelation(Member familyMember, List<RelationShip> relationShips) {
        List<String> relationNames = new ArrayList<>();
        relationService.findRelatives(relationShips.iterator(), singletonList(familyMember), relationNames);
        return relationNames;
    }

    private Result resultForGetRelation(List<String> relationNames) {
        String message = String.join(" ", relationNames);
        boolean isSuccess = !message.isEmpty();
        message = !isSuccess ? Constants.NO_REALATIONS : message;
        return new Result(message, isSuccess);
    }

    public Result findOlderGenerationMember(String firstMemberName, String secondMemberName) {

        return null;
    }
}
