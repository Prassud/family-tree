package com.geektrust.familytree.exception;

public class FamilyTreeException extends RuntimeException {

	public FamilyTreeException(String message, Exception ex) {
		super(message, ex);
	}

	public FamilyTreeException(String message) {
		super(message);
	}

}
