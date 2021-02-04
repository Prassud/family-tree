package com.geektrust.familytree;

import com.geektrust.familytree.command.AbstractCommand;
import com.geektrust.familytree.command.AddMemberCommand;
import com.geektrust.familytree.command.GetMembersCommand;
import com.geektrust.familytree.exception.FamilyTreeException;
import com.geektrust.familytree.manager.FamilyTreeManager;
import com.geektrust.familytree.model.Result;
import com.geektrust.familytree.service.FamilyTreeService;
import com.geektrust.familytree.service.RelationService;
import com.geektrust.familytree.utils.RelationShipManager;

import java.io.*;
import java.util.*;

public class FamilyTreeApplication {

	private Map<String, AbstractCommand> commands;

	private void initialize() {
		commands = new HashMap<>();
		String relationShipMapperFile = "relationship_mapper.json";

		RelationShipManager.getInstance().load(relationShipMapperFile);
		FamilyTreeManager.initialize();
		RelationService relationService = new RelationService();
		FamilyTreeService familyTreeService = new FamilyTreeService(relationService);

		commands.put("ADD_CHILD", new AddMemberCommand(familyTreeService));
		commands.put("GET_RELATIONSHIP", new GetMembersCommand(familyTreeService));
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 0 || args.length > 1) {
			throw new IllegalArgumentException("Invalid input file");
		}

		FamilyTreeApplication application = new FamilyTreeApplication();
		application.initialize();
		String inputFilePath = args[0];
		String inputLine;

		BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilePath));
		while ((inputLine = bufferedReader.readLine()) != null) {
			inputLine = inputLine.replaceAll("(\\\\ )", "-");
			String[] input = inputLine.split(" ");
			AbstractCommand abstractCommand = application.commands.get(input[0]);
			if (Objects.isNull(abstractCommand)) {
				throw new FamilyTreeException("Invalid Command");
			}

			Result executionResult = abstractCommand.execute(input);
			System.out.println(executionResult.getMessage());
		}
	}
}