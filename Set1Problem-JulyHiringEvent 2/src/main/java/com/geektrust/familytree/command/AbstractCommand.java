package com.geektrust.familytree.command;

import com.geektrust.familytree.model.Member;
import com.geektrust.familytree.model.Result;
import com.geektrust.familytree.service.FamilyTreeService;

public abstract class AbstractCommand {

	protected FamilyTreeService familyTreeService;

	public AbstractCommand(FamilyTreeService familyTreeService) {
		this.familyTreeService = familyTreeService;
	}

	public abstract Result execute(String[] input);
}
