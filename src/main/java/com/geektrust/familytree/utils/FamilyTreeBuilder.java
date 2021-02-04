package com.geektrust.familytree.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geektrust.familytree.exception.FamilyTreeException;
import com.geektrust.familytree.model.Family;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class FamilyTreeBuilder {
	public static Family build(String builderFileName) {
		try {
			InputStream resourceAsStream = FamilyTreeBuilder.class.getClassLoader().getResourceAsStream(builderFileName);
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(new InputStreamReader(resourceAsStream), Family.class);
		} catch (IOException | NullPointerException ex) {
			throw new FamilyTreeException("Failed to build the family Tree", ex);
		}
	}
}
