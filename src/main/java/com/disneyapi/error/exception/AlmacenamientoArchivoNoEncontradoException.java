package com.disneyapi.error.exception;

public class AlmacenamientoArchivoNoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 2213641885184176625L;

	public AlmacenamientoArchivoNoEncontradoException(String mensaje) {
		super(mensaje);
	}

	public AlmacenamientoArchivoNoEncontradoException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
