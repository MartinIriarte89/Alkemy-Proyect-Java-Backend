package com.disneyapi.error.exception;

import org.springframework.validation.Errors;

public class ValidacionException extends RuntimeException {

	private static final long serialVersionUID = 4725137193770848720L;
	private Errors errores;
	
	public ValidacionException(Errors errores) {
		this.errores = errores;
	}
	
	public Errors getErrores() {
		return this.errores;
	}
}
