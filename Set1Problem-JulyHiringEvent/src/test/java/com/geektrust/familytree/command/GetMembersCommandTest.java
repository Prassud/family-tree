package com.geektrust.familytree.command;

import com.geektrust.familytree.service.FamilyTreeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class GetMembersCommandTest {
	@Mock
	private FamilyTreeService familyTreeService;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void shouldInvokeFamilyTreeServiceCreateMemberMethod() {
		GetMembersCommand command = new GetMembersCommand(familyTreeService);
		String input[] = new String[] {"", "mother", "Son"};
		command.execute(input);

		verify(familyTreeService).getFamilyMember("mother", "Son");
	}
}