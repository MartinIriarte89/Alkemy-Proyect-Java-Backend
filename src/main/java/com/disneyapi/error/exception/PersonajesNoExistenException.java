package com.disneyapi.error.exception;

public class PersonajesNoExistenException extends RuntimeException {

	private static final long serialVersionUID = -4430930995394326098L;

	public PersonajesNoExistenException(String personajesIds) {
		super("El o los personajes con id: " + personajesIds + " no existen.");
	}
}
