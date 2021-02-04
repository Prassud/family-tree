package com.geektrust.familytree.command;

import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.model.Result;
import com.geektrust.familytree.service.FamilyTreeService;

public class GetMembersCommand extends AbstractCommand {
	public GetMembersCommand(FamilyTreeService familyTreeService) {
		super(familyTreeService);
	}

	@Override
	public Result execute(String[] input) {
		String memberName = input[1].replace('-',' ');
		String relationShip = input[2];

		return familyTreeService.getFamilyMember(memberName, relationShip);
	}
}
