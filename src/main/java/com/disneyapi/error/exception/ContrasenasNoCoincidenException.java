package com.disneyapi.error.exception;

public class ContrasenasNoCoincidenException extends RuntimeException {
	
	private static final long serialVersionUID = -3356510533951835344L;

	public ContrasenasNoCoincidenException() {
		super("Las contraseñas deben ser idénticas");
	}
}
