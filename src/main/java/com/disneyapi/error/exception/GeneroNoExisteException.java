package com.disneyapi.error.exception;

public class GeneroNoExisteException extends RuntimeException {

	private static final long serialVersionUID = -4342119598455004443L;

	public GeneroNoExisteException(long generoId) {
		super("El género con id: " + generoId + " no existe.");
	}
}
