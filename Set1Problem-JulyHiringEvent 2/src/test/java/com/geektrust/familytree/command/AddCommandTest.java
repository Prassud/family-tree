package com.geektrust.familytree.command;

import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.service.FamilyTreeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AddCommandTest {

	@Mock
	private FamilyTreeService familyTreeService;

	@Before
	public void setUp() {
		initMocks(this);
	}


	@Test
	public void shouldInvokeFamilyTreeServiceCreateMemberMethod() {
		AddMemberCommand command = new AddMemberCommand(familyTreeService);
		String input[] = new String[] {"", "mother", "child", "Male"};
		command.execute(input);
		verify(familyTreeService).addChildren("mother", Member.Gender.MALE, "child");

		reset(familyTreeService);
		input = new String[] {"", "mother", "child2", "Female"};
		command.execute(input);
		verify(familyTreeService).addChildren("mother", Member.Gender.FEMALE, "child2");
	}
}