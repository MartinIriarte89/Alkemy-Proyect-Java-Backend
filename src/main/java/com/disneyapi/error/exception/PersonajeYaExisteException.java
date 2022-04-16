package com.disneyapi.error.exception;

public class PersonajeYaExisteException extends RuntimeException{

	private static final long serialVersionUID = 4033524053720652790L;

	public PersonajeYaExisteException(String nombrePersonaje) {
		super("El personaje " + nombrePersonaje + " ya existe");
	}
}
