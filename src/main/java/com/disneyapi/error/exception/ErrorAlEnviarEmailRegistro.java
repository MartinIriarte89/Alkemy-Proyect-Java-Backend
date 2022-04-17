package com.disneyapi.error.exception;

public class ErrorAlEnviarEmailRegistro extends RuntimeException {

	private static final long serialVersionUID = 4972543520050986224L;

	public ErrorAlEnviarEmailRegistro() {
		super("Ha ocurrido un erro al querer enviar el mail a su casilla de correo");
	}
}
