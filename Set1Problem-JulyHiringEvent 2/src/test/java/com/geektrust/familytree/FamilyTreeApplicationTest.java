package com.geektrust.familytree;

import com.geektrust.familytree.command.AddMemberCommand;
import com.geektrust.familytree.command.GetMembersCommand;
import com.geektrust.familytree.exception.FamilyTreeException;
import com.geektrust.familytree.model.Result;
import com.geektrust.familytree.service.FamilyTreeService;
import com.geektrust.familytree.service.RelationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {FamilyTreeApplication.class,
		RelationService.class, FamilyTreeService.class, AddMemberCommand.class, GetMembersCommand.class})
public class FamilyTreeApplicationTest {

	@Test
	public void shouldReadFromInputFile() throws IOException {
		String fileName = getClass().getClassLoader().getResource("inputFile.txt").getFile();
		String[] args = new String[] {fileName};
		FamilyTreeApplication.main(args);
	}

	@Test
	public void shouldVerifyInitializationOfAbstractCommands() throws Exception {
		String fileName = getClass().getClassLoader().getResource("inputFile.txt").getFile();
		String[] args = new String[] {fileName};
		ArgumentCaptor<String[]> addMembercaptor = ArgumentCaptor.forClass(String[].class);
		ArgumentCaptor<String[]> getMemberCaptor = ArgumentCaptor.forClass(String[].class);
		FamilyTreeService fts = mock(FamilyTreeService.class);
		AddMemberCommand addMemberCommand = mock(AddMemberCommand.class);
		GetMembersCommand getMembersCommand = mock(GetMembersCommand.class);
		Result expectedResult = new Result("Done", true);
		mockComandsAndServices(fts, addMemberCommand, getMembersCommand, expectedResult);

		FamilyTreeApplication.main(args);

		verifyServiceInitialization(fts);
		Mockito.verify(addMemberCommand).execute(addMembercaptor.capture());
		Mockito.verify(getMembersCommand).execute(getMemberCaptor.capture());
		assertCommandExecution(addMembercaptor, getMemberCaptor);
	}

	private void assertCommandExecution(ArgumentCaptor<String[]> addMembercaptor, ArgumentCaptor<String[]> getMemberCaptor) {
		assertEquals("ADD_CHILD", addMembercaptor.getValue()[0]);
		assertEquals("Chitra", addMembercaptor.getValue()[1]);
		assertEquals("Aria", addMembercaptor.getValue()[2]);
		assertEquals("Female", addMembercaptor.getValue()[3]);

		assertEquals("GET_RELATIONSHIP", getMemberCaptor.getValue()[0]);
		assertEquals("Lavnya", getMemberCaptor.getValue()[1]);
		assertEquals("Maternal-Aunt", getMemberCaptor.getValue()[2]);
	}

	private void mockComandsAndServices(FamilyTreeService fts,
	                                    AddMemberCommand addMemberCommand,
	                                    GetMembersCommand getMembersCommand,
	                                    Result expectedResult) throws Exception {
		Mockito.when(addMemberCommand.execute(any())).thenReturn(expectedResult);
		Mockito.when(getMembersCommand.execute(any())).thenReturn(expectedResult);
		PowerMockito.whenNew(AddMemberCommand.class).withAnyArguments().thenReturn(addMemberCommand);
		PowerMockito.whenNew(GetMembersCommand.class).withAnyArguments().thenReturn(getMembersCommand);
		PowerMockito.whenNew(FamilyTreeService.class).withAnyArguments().thenReturn(fts);
	}

	private void verifyServiceInitialization(FamilyTreeService familyTreeService) throws Exception {
		PowerMockito.verifyNew(AddMemberCommand.class).withArguments(familyTreeService);
		PowerMockito.verifyNew(GetMembersCommand.class).withArguments(familyTreeService);
		PowerMockito.verifyNew(FamilyTreeService.class).withArguments(any(RelationService.class));
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfArgsLengthIsGreaterThanOne() throws IOException {
		String fileName = getClass().getClassLoader().getResource("inputFile.txt").getFile();
		String[] args = new String[] {"", fileName};
		FamilyTreeApplication.main(args);
	}

	@Test(expected = FamilyTreeException.class)
	public void shouldThrowFamilyTreeExceptionIfCommandIsNotFound() throws IOException {
		String fileName = getClass().getClassLoader().getResource("inputFile1.txt").getFile();
		String[] args = new String[] {fileName};
		FamilyTreeApplication.main(args);
	}

	@Test
	public void   integrationTest() throws Exception {
		String fileName = getClass().getClassLoader().getResource("integration.txt").getFile();
		String[] args = new String[] {fileName};
		FamilyTreeApplication.main(args);
	}
}