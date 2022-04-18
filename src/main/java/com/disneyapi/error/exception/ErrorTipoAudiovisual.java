package com.disneyapi.error.exception;

public class ErrorTipoAudiovisual extends RuntimeException {

	private static final long serialVersionUID = 3338143398332532196L;

	public ErrorTipoAudiovisual(String tipo) {
		super("El tipo " + tipo + " no es valido. Por favor seleccione pelicula o serie.");
	}

}
