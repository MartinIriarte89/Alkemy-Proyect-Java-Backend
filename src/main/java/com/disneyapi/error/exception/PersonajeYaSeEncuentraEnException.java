package com.disneyapi.error.exception;

public class PersonajeYaSeEncuentraEnException extends RuntimeException {

	private static final long serialVersionUID = -8202418724094868086L;

	public PersonajeYaSeEncuentraEnException(String nombre) {
		super("El personaje " + nombre + " ya se encontraba en la pelicula/serie");
	}

}
