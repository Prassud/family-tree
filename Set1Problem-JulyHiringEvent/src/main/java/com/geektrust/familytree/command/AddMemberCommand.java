package com.geektrust.familytree.command;

import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.model.Result;
import com.geektrust.familytree.service.FamilyTreeService;

public class AddMemberCommand extends AbstractCommand {
	public AddMemberCommand(FamilyTreeService familyTreeService) {
		super(familyTreeService);
	}

	@Override
	public Result execute(String[] input) {
		String motherNmae = input[1];
		String childName = input[2];
		String childGender = input[3];

		return familyTreeService.addChildren(motherNmae, Member.Gender.valueOf(childGender.toUpperCase()), childName);
	}
}
