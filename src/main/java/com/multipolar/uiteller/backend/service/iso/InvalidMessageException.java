package com.multipolar.uiteller.backend.service.iso;

public class InvalidMessageException extends RuntimeException {

	private static final long serialVersionUID = -794796414856326861L;

	public InvalidMessageException(String message) {
		super(message);
	}
}
