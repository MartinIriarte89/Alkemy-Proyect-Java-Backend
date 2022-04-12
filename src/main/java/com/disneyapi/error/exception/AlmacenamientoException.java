package com.disneyapi.error.exception;

public class AlmacenamientoException extends RuntimeException {

	private static final long serialVersionUID = -5721224236283682938L;

	public AlmacenamientoException(String mensaje) {
		super(mensaje);
	}

	public AlmacenamientoException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
