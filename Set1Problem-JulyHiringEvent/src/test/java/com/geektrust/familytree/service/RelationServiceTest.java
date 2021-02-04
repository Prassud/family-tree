package com.geektrust.familytree.service;

import com.geektrust.familytree.manager.FamilyTreeManager;
import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.model.Relation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.geektrust.familytree.bean.RelationShipMapBean.RelationShip;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {FamilyTreeManager.class})
public class RelationServiceTest {

	private RelationService relationService;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
		relationService = new RelationService();

		PowerMockito.mockStatic(FamilyTreeManager.class);
	}

	@Test
	public void shouldUpdateChildrenInfoToMotherAndFather() {
		Map<Relation.Type, List<String>> fatherRelations = new HashMap<>();
		Map<Relation.Type, List<String>> motherRelations = new HashMap<>();

		motherRelations.put(Relation.Type.SPOUSE, new ArrayList<>(Collections.singletonList("father")));
		motherRelations.put(Relation.Type.CHILDREN, new ArrayList<>(Collections.singletonList("chilfff")));
		Member mother = new Member("mother", Member.Gender.FEMALE, motherRelations);
		Member father = new Member("father", Member.Gender.FEMALE, fatherRelations);
		Member child = new Member("child", Member.Gender.FEMALE, null);

		relationService.updateChildrenInfo(mother, father, child);
		assertTrue(mother.getRelation(Relation.Type.CHILDREN).contains("chilfff"));
		assertTrue(mother.getRelation(Relation.Type.CHILDREN).contains("child"));
		assertTrue(father.getRelation(Relation.Type.CHILDREN).contains("child"));
	}

	@Test
	public void shouldUpdateSiblingInfo() {
		Map<Relation.Type, List<String>> child2Relation = new HashMap<>();
		Map<Relation.Type, List<String>> child1Relation = new HashMap<>();
		child1Relation.put(Relation.Type.SIBLING, new ArrayList<>(Collections.singletonList("child2")));
		child2Relation.put(Relation.Type.SIBLING, new ArrayList<>(Collections.singletonList("child1")));
		Member child1 = new Member("child1", Member.Gender.FEMALE, child1Relation);
		Member child2 = new Member("child2", Member.Gender.FEMALE, child2Relation);
		List<Member> children = new ArrayList<>();
		children.add(child1);
		children.add(child2);
		Member child = new Member("child", Member.Gender.FEMALE, null);

		relationService.addSiblingsTo(child, children);

		assertTrue(child1.getRelation(Relation.Type.SIBLING).contains("child2"));
		assertTrue(child1.getRelation(Relation.Type.SIBLING).contains("child"));
		assertFalse(child1.getRelation(Relation.Type.SIBLING).contains("child1"));

		assertTrue(child2.getRelation(Relation.Type.SIBLING).contains("child"));
		assertTrue(child2.getRelation(Relation.Type.SIBLING).contains("child1"));
		assertFalse(child2.getRelation(Relation.Type.SIBLING).contains("child2"));

		assertTrue(child.getRelation(Relation.Type.SIBLING).contains("child2"));
		assertTrue(child.getRelation(Relation.Type.SIBLING).contains("child1"));
		assertFalse(child.getRelation(Relation.Type.SIBLING).contains("child"));
	}

	@Test
	public void shouldAddAllFamilyMemberNamesToResultantListWhenRelationShipChainHasNoNextElement() {
		List<String> resultantString = new ArrayList<>();
		List<RelationShip> relationShips = new ArrayList<>();
		List<Member> familyMembers = new ArrayList<>();
		familyMembers.add(new Member("prasanna", Member.Gender.MALE));

		relationService.findRelatives(relationShips.iterator(), familyMembers, resultantString);
		assertEquals(resultantString.get(0), "prasanna");
		assertTrue(resultantString.size() == 1);
	}

	@Test
	public void shouldReturnResultantStringAsEmptyWhenThereIsNoRelationForRelationShip() {
		List<String> resultantString = new ArrayList<>();
		List<RelationShip> relationShips = setUpRelationShips(Member.Gender.MALE);
		List<Member> familyMembers = new ArrayList<>();
		familyMembers.add(new Member("test prasanna", Member.Gender.MALE));

		relationService.findRelatives(relationShips.iterator(), familyMembers, resultantString);

		assertEquals(resultantString.size(), 0);
	}

	@Test
	public void shouldReturnResultantStringWhenThereIsARelationForRelationShipForOneFamilyMember() {
		FamilyTreeManager manager = mock(FamilyTreeManager.class);
		PowerMockito.when(FamilyTreeManager.getInstance()).thenReturn(manager);
		List<String> expectedResult = new ArrayList<>();

		// set up relationship chain
		List<RelationShip> relationShips = new ArrayList<>();
		RelationShip relationShip = new RelationShip(Relation.Type.SPOUSE, Member.Gender.FEMALE);
		relationShips.add(relationShip);

		// set up family tree at one level
		List<Member> familyMembers = new ArrayList<>();
		List<String> spouseList = Stream.of("Prasanna Spouse").collect(Collectors.toList());
		Map<Relation.Type, List<String>> relations = new HashMap<>();
		relations.put(Relation.Type.SPOUSE, spouseList);

		Member spouse = new Member("Prasanna Spouse", Member.Gender.FEMALE, relations);
		when(manager.getMembersByName(spouseList)).thenReturn(Stream.of(spouse));
		Member prasanna = new Member("prasanna", Member.Gender.MALE, relations);
		familyMembers.add(prasanna);

		relationService.findRelatives(relationShips.iterator(), familyMembers, expectedResult);

		assertEquals(expectedResult.size(), 1);
		assertEquals(expectedResult.get(0), "Prasanna Spouse");
	}

	@Test
	public void shouldReturnResultantStringWhenThereIsARelationForRelationShipForOneFamilyMemberWithoutGenderCondition() {
		FamilyTreeManager manager = mock(FamilyTreeManager.class);
		PowerMockito.when(FamilyTreeManager.getInstance()).thenReturn(manager);
		List<String> expectedResultNames = new ArrayList<>();
		List<RelationShip> relationShips = new ArrayList<>();
		RelationShip relationShip = new RelationShip(Relation.Type.SPOUSE, null);
		relationShips.add(relationShip);

		List<Member> familyMembers = new ArrayList<>();
		Map<Relation.Type, List<String>> relations = new HashMap<>();
		List<String> spouseList = Stream
				.of("Prasanna 1st Spouse", "Prasanna 2nd Spouse")
				.collect(Collectors.toList());
		relations.put(Relation.Type.SPOUSE, spouseList);


		Member spouse1 = new Member("Prasanna 1st Spouse", Member.Gender.MALE, relations);
		Member spouse2 = new Member("Prasanna 2nd Spouse", Member.Gender.FEMALE, relations);

		when(manager.getMembersByName(any())).thenReturn(Stream.of(spouse1, spouse2));
		Member prasanna = new Member("test prasanna", Member.Gender.MALE, relations);
		familyMembers.add(prasanna);

		relationService.findRelatives(relationShips.iterator(), familyMembers, expectedResultNames);

		assertEquals(expectedResultNames.size(), 2);
		assertEquals(expectedResultNames.get(0), "Prasanna 1st Spouse");
		assertEquals(expectedResultNames.get(1), "Prasanna 2nd Spouse");
	}

	@Test
	public void shouldReturnResultantStringWhenThereIsMoreThanOneRelationForAFamilyMember() {
		FamilyTreeManager manager = mock(FamilyTreeManager.class);
		PowerMockito.when(FamilyTreeManager.getInstance()).thenReturn(manager);
		List<String> resultantString = new ArrayList<>();
		List<RelationShip> relationShips = new ArrayList<>();
		RelationShip relationShip = new RelationShip(Relation.Type.SPOUSE, Member.Gender.FEMALE);
		relationShips.add(relationShip);

		List<Member> familyMembers = new ArrayList<>();
		Map<Relation.Type, List<String>> relations = new HashMap<>();
		List<String> spouseList = Stream
				.of("Prasanna 1st Spouse", "Prasanna 2nd Spouse")
				.collect(Collectors.toList());
		relations.put(Relation.Type.SPOUSE, spouseList);


		Member spouse1 = new Member("Prasanna 1st Spouse", Member.Gender.FEMALE, relations);
		Member spouse2 = new Member("Prasanna 2nd Spouse", Member.Gender.FEMALE, relations);

		when(manager.getMembersByName(any())).thenReturn(Stream.of(spouse1, spouse2));
		Member prasanna = new Member("test prasanna", Member.Gender.MALE, relations);
		familyMembers.add(prasanna);

		relationService.findRelatives(relationShips.iterator(), familyMembers, resultantString);

		assertEquals(resultantString.size(), 2);
		assertEquals(resultantString.get(0), "Prasanna 1st Spouse");
		assertEquals(resultantString.get(1), "Prasanna 2nd Spouse");
	}


	@Test
	public void shouldReturnResultantStringWhenRelationshipChainIsMoreThanOneForAFamilyMember() {
		FamilyTreeManager manager = mock(FamilyTreeManager.class);
		PowerMockito.when(FamilyTreeManager.getInstance()).thenReturn(manager);
		List<String> actualResponse = new ArrayList<>();

		// set up relationship chain
		List<RelationShip> relationShips = setUpRelationShips(Member.Gender.FEMALE);

		//set up family member chain
		List<Member> familyMembers = new ArrayList<>();

		List<String> spouseList = Stream.of("Prasanna 1st Spouse").collect(Collectors.toList());
		List<String> spouseSisterList = Stream.of("SpouseSister").collect(Collectors.toList());

		Map<Relation.Type, List<String>> relations = new HashMap<>();
		relations.put(Relation.Type.SIBLING, spouseSisterList);
		Member spouse = new Member("Prasanna 1st Spouse", Member.Gender.FEMALE, relations);
		Member spouseSister = new Member("Prasanna 1st Spouse Sister", Member.Gender.FEMALE, new HashMap<>());

		relations = new HashMap<>();
		relations.put(Relation.Type.SPOUSE, spouseList);
		Member prasanna = new Member("test prasanna", Member.Gender.MALE, relations);
		familyMembers.add(prasanna);

		when(manager.getMembersByName(spouseList)).thenReturn(Stream.of(spouse));
		when(manager.getMembersByName(spouseSisterList)).thenReturn(Stream.of(spouseSister));

		relationService.findRelatives(relationShips.iterator(), familyMembers, actualResponse);

		assertEquals(actualResponse.size(), 1);
		assertEquals(actualResponse.get(0), "Prasanna 1st Spouse Sister");
	}

	@Test
	public void shouldReturnResultantStringWhenRelationshipChainIsMoreThanOneForMultipleFamilyMember() {
		FamilyTreeManager manager = mock(FamilyTreeManager.class);
		PowerMockito.when(FamilyTreeManager.getInstance()).thenReturn(manager);
		List<String> actualResponse = new ArrayList<>();

		// set up relationship chain
		List<RelationShip> relationShips = setUpRelationShips(Member.Gender.FEMALE);

		//set up family member chain
		List<Member> familyMembers = new ArrayList<>();

		List<String> spouseList = Stream.of("Prasanna 1st Spouse", "Prasanna 2nd Spouse").collect(Collectors.toList());
		List<String> spouseSisterList = Stream.of("SpouseSister").collect(Collectors.toList());

		// setup first spouse relations
		Map<Relation.Type, List<String>> relations = new HashMap<>();
		relations.put(Relation.Type.SIBLING, spouseSisterList);
		Member firstSpouse = new Member("Prasanna 1st Spouse", Member.Gender.FEMALE, relations);
		Member spouseSister = new Member("Prasanna 1st Spouse Sister", Member.Gender.FEMALE, new HashMap<>());

		//set second spouse relations
		relations = new HashMap<>();
		List<String> seecondSpouseSisterList = Stream.of("SecondSpouseSister").collect(Collectors.toList());
		relations.put(Relation.Type.SIBLING, seecondSpouseSisterList);
		Member secondSpouse = new Member("Prasanna 2nd Spouse", Member.Gender.FEMALE, relations);
		Member secondSpouseSister = new Member("Prasanna 2nd Spouse Sister", Member.Gender.FEMALE, new HashMap<>());


		//set up relations
		relations = new HashMap<>();
		relations.put(Relation.Type.SPOUSE, spouseList);
		Member prasanna = new Member("Prasanna", Member.Gender.MALE, relations);
		familyMembers.add(prasanna);

		when(manager.getMembersByName(spouseList)).thenReturn(Stream.of(firstSpouse, secondSpouse));
		when(manager.getMembersByName(spouseSisterList)).thenReturn(Stream.of(spouseSister));
		when(manager.getMembersByName(seecondSpouseSisterList)).thenReturn(Stream.of(secondSpouseSister));

		relationService.findRelatives(relationShips.iterator(), familyMembers, actualResponse);

		assertEquals(actualResponse.size(), 2);
		assertEquals(actualResponse.get(0), "Prasanna 1st Spouse Sister");
		assertEquals(actualResponse.get(1), "Prasanna 2nd Spouse Sister");
	}

	private List<RelationShip> setUpRelationShips(Member.Gender gender) {
		List<RelationShip> relationShips = new ArrayList<>();
		RelationShip relationShip = new RelationShip(Relation.Type.SPOUSE, gender);
		relationShips.add(relationShip);
		relationShip = new RelationShip(Relation.Type.SIBLING, Member.Gender.FEMALE);
		relationShips.add(relationShip);
		return relationShips;
	}

}