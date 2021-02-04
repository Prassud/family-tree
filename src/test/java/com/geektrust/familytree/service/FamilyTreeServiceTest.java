package com.geektrust.familytree.service;

import com.geektrust.familytree.BaseTest;
import com.geektrust.familytree.bean.RelationShipMapBean;
import com.geektrust.familytree.manager.FamilyTreeManager;
import com.geektrust.familytree.model.Family;
import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.model.Relation;
import com.geektrust.familytree.model.Relation.Type;
import com.geektrust.familytree.model.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {FamilyTreeManager.class})
public class FamilyTreeServiceTest extends BaseTest {

	private FamilyTreeService familyTreeService;

	@Mock
	private RelationService relationService;

	@Mock
	private FamilyTreeManager manager;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		initMocks(this);
		family = setBasicFamilyTree(null);
		familyTreeService = new FamilyTreeService(relationService);
		Answer<Member> memberAnswer = invocation -> family.getMember((String) invocation.getArguments()[0]);
		manager = mock(FamilyTreeManager.class);
		PowerMockito.mockStatic(FamilyTreeManager.class);
		doAnswer(memberAnswer).when(manager).getMember(any());

		Answer addAnswer = invocation -> {
			family.addMember((String) invocation.getArguments()[0], (Member) invocation.getArguments()[1]);
			return null;
		};
		doAnswer(addAnswer).when(manager).addMember(any(), any());
		PowerMockito.when(FamilyTreeManager.getInstance()).thenReturn(manager);
	}

	@Test
	public void shouldAddNewFamilyMemberToTheMother() {
		when(manager.getMembersByName(Arrays.asList("son"))).thenReturn(Stream.of(family.getMember("son")));
		Result result = familyTreeService.addChildren("mother", Member.Gender.MALE, "child");
		Member child = family.getMember("child");

		assertChildInfoUpdation();
		assertTrue(result.isSuccess());
		assertTrue(child.getRelation(Type.PARENT)
				.equals(Arrays.asList("mother", "father")));
		assertSiblingInfoAddition();
		assertEquals("CHILD_ADDITION_SUCCEEDED", result.getMessage());
		assertNotNull(this.family.getMember("child"));
	}

	@Test
	public void shouldReturnResultAsFailedOnAddingANewChildrenToAMaleMember() {
		Member mother = new Member("mother", Member.Gender.MALE);
		this.family = setBasicFamilyTree(mother);
		Result result = familyTreeService.addChildren("mother", Member.Gender.MALE, "child");

		assertEquals("CHILD_ADDITION_FAILED", result.getMessage());
	}

	@Test
	public void shouldReturnResultAsFailedOnAddingANewChildrenToAMotherWhoHasNoFather() {
		Member mother = new Member("mother", Member.Gender.MALE);
		this.family = setBasicFamilyTree(mother);
		Result result = familyTreeService.addChildren("mother", Member.Gender.MALE, "child");

		FamilyTreeManager manager = mock(FamilyTreeManager.class);
		when(manager.getFamily()).thenReturn(family);
		assertEquals("CHILD_ADDITION_FAILED", result.getMessage());
	}

	private void assertSiblingInfoAddition() {
		ArgumentCaptor<Member> siblingInfoCaptor = ArgumentCaptor.forClass(Member.class);
		ArgumentCaptor<List> siblingInfoListCaptor = ArgumentCaptor.forClass(List.class);
		verify(relationService).addSiblingsTo(siblingInfoCaptor.capture(), siblingInfoListCaptor.capture());

		Member siblingInfoArgs = siblingInfoCaptor.getValue();
		assertEquals("child", siblingInfoArgs.getName());
		List siblings = siblingInfoListCaptor.getValue();
		assertEquals("son", ((Member) siblings.get(0)).getName());
	}


	private void assertChildInfoUpdation() {
		ArgumentCaptor<Member> childrenInfoCaptor = ArgumentCaptor.forClass(Member.class);
		verify(relationService, times(1))
				.updateChildrenInfo(childrenInfoCaptor.capture(), childrenInfoCaptor.capture(), childrenInfoCaptor.capture());
		List<Member> childrenInfoArgs = childrenInfoCaptor.getAllValues();
		assertEquals("mother", childrenInfoArgs.get(0).getName());
		assertEquals("father", childrenInfoArgs.get(1).getName());
		assertEquals("child", childrenInfoArgs.get(2).getName());
	}

	@Test
	public void shouldCallRelationServiceWithMemberNameAndRelationShipForSingleRelationShipChain() {
		doAnswer((invocation) -> {
			List<String> resultantString = (List<String>) invocation.getArguments()[2];
			resultantString.add("son");
			return null;
		}).when(relationService).findRelatives(any(), any(), any());
		Result result = familyTreeService.getFamilyMember("father", "Son");

		ArgumentCaptor<List> resulatantListCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<List> familyMemberCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Iterator> relationShipIterator = ArgumentCaptor.forClass(Iterator.class);
		verify(relationService).findRelatives(relationShipIterator.capture(),
				familyMemberCaptor.capture(), resulatantListCaptor.capture());
		List<Iterator> relationshipIteratorVal = relationShipIterator.getAllValues();
		List<List> familyMemberValues = familyMemberCaptor.getAllValues();
		List<List> resultantListValues = resulatantListCaptor.getAllValues();
		Iterator iterator = relationshipIteratorVal.get(0);
		List familyMembers = familyMemberValues.get(0);
		List resultList = resultantListValues.get(0);

		assertFindRelationCaptors(iterator, familyMembers, resultList, "son", Type.CHILDREN, Member.Gender.MALE);

		assertTrue(result.isSuccess());
		assertEquals("son", result.getMessage());
	}

	@Test
	public void shouldCallRelationServiceWithMemberNameAndRelationShipForMultipleRelationShipChain() {
		doAnswer((invocation) -> {
			List<String> resultantString = (List<String>) invocation.getArguments()[2];
			//if (!CollectionUtils.isEmpty(resultantString)) {
			resultantString.add("Sister-In-Law");
			//}
			return null;
		}).when(relationService).findRelatives(any(), any(), any());
		Result result = familyTreeService.getFamilyMember("father", "Sister-In-Law");

		ArgumentCaptor<List> resulatantListCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<List> familyMemberCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<Iterator> relationShipIterator = ArgumentCaptor.forClass(Iterator.class);
		verify(relationService, times(2)).findRelatives(relationShipIterator.capture(),
				familyMemberCaptor.capture(), resulatantListCaptor.capture());
		List<Iterator> relationshipIteratorVal = relationShipIterator.getAllValues();
		List<List> familyMemberValues = familyMemberCaptor.getAllValues();
		List<List> resultantListValues = resulatantListCaptor.getAllValues();

		Iterator iterator = relationshipIteratorVal.get(0);
		List familyMembers = familyMemberValues.get(0);
		List resultList = resultantListValues.get(0);

		assertFindRelationCaptors(iterator, familyMembers,
				resultList, "Sister-In-Law", Type.SIBLING,
				Member.Gender.MALE);


		iterator = relationshipIteratorVal.get(1);
		familyMembers = familyMemberValues.get(1);
		resultList = resultantListValues.get(1);

		assertFindRelationCaptors(iterator, familyMembers,
				resultList, "Sister-In-Law", Type.SPOUSE, null);


		assertTrue(result.isSuccess());
		assertEquals("Sister-In-Law Sister-In-Law", result.getMessage());
	}

	private void assertFindRelationCaptors(Iterator iterator,
	                                       List familyMembers,
	                                       List resultList,
	                                       String familyMemberRelationName,
	                                       Type type, Member.Gender gender) {
		RelationShipMapBean.RelationShip capturedRelationShip = (RelationShipMapBean.RelationShip) iterator.next();
		assertEquals(capturedRelationShip.getGender(), gender);
		assertEquals(capturedRelationShip.getRelationType(), type);

		Member member = (Member) familyMembers.get(0);
		assertEquals(member.getName(), "father");
		assertEquals(member.getGender(), Member.Gender.MALE);
		assertTrue(resultList.get(0).equals(familyMemberRelationName));
	}

	@Test
	public void shouldReturnPersonNotFoundIfMemberRelationIsNotFound() {
		Result result = familyTreeService.getFamilyMember("father", "Daughter");

		assertFalse(result.isSuccess());
		assertEquals("NONE", result.getMessage());
	}

	@Test
	public void shouldReturnPersonNotFoundIfRelationShipIsNotFoundInMultipLeRelationAndRelationShipChain() {
		Result result = familyTreeService.getFamilyMember("father", "abc");

		assertFalse(result.isSuccess());
		assertEquals("PERSON_NOT_FOUND", result.getMessage());
	}

	@Test
	public void shouldReturnPersonNotFoundIfMemberForARelationIsNotFound() {
		Result result = familyTreeService.getFamilyMember("son", "Paternal-Uncle");

		assertFalse(result.isSuccess());
		assertEquals("NONE", result.getMessage());
	}

	@Test
	public void shouldReturnGenerationForGivenMember() {
		Member rootGenMember = new Member("gen0", Member.Gender.MALE);
		Map<Relation.Type, List<String>> relationshipMap = new HashMap<>();
		ArrayList<String> relations = new ArrayList<>();
		relations.add("gen0");
		relationshipMap.put(Relation.Type.PARENT, relations);
		Member firstGenMember = new Member("gen1", Member.Gender.MALE, relationshipMap);
		when(manager.getMember("gen1")).thenReturn(firstGenMember);
		when(manager.getMember("gen0")).thenReturn(rootGenMember);

		Result olderGenerationMember = familyTreeService.findOlderGenerationMember("gen0", "gen1");

		assertEquals(olderGenerationMember.getMessage(),"gen0");
	}

	private Family setBasicFamilyTree(Member mother) {
		Map<Type, List<String>> relations = new HashMap<>(10);
		List<String> relationNames = new ArrayList<>();
		String motherName = nonNull(mother) ? mother.getName() : "mother";
		relationNames.add(motherName);
		relations.put(Type.SPOUSE, relationNames);
		Member father = new Member("father", Member.Gender.MALE, relations);

		relationNames = new ArrayList<>();
		relationNames.add("father");
		relations.put(Type.SPOUSE, relationNames);

		relations.put(Type.CHILDREN, Collections.singletonList("son"));
		mother = nonNull(mother) ? mother : new Member(motherName, Member.Gender.FEMALE, relations);

		relationNames = new ArrayList<>();
		relationNames.add("father");
		relationNames.add(motherName);
		relations.put(Type.PARENT, relationNames);
		relations.put(Type.CHILDREN, Collections.singletonList("son"));
		Member son = new Member("son", Member.Gender.MALE, relations);


		Map<String, Member> members = new HashMap<>();
		members.put("father", father);
		members.put(mother.getName(), mother);
		members.put(son.getName(), son);
		Family family = new Family(members);
		return family;
	}
}