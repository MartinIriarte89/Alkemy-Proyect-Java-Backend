package com.disneyapi.error.exception;

public class UsuarioNoEncontradoException extends RuntimeException{
	
	private static final long serialVersionUID = -162231999082998026L;

	public UsuarioNoEncontradoException(String nombreUsuario) {
		super("El usuario: " + nombreUsuario + " no se econtro.");
	}
}
