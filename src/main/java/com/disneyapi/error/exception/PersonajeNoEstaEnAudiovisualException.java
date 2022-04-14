package com.disneyapi.error.exception;

public class PersonajeNoEstaEnAudiovisualException extends RuntimeException {

	private static final long serialVersionUID = 666862969121471574L;

	public PersonajeNoEstaEnAudiovisualException(String nombre) {
		super("El personaje " + nombre + " no se encuentra en la película/serie");
	}

}
