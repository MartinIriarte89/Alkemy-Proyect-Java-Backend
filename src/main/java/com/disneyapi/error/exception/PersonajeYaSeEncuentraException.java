package com.disneyapi.error.exception;

public class PersonajeYaSeEncuentraException extends RuntimeException {

	private static final long serialVersionUID = -8202418724094868086L;

	public PersonajeYaSeEncuentraException(String nombre) {
		super("El personaje " + nombre + " ya se encontraba en la pelicula/serie");
	}

}
