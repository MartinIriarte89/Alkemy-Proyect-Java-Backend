package com.disneyapi.error.exception;

public class UsuarioYaExisteException extends RuntimeException {

	private static final long serialVersionUID = -8365097836219545516L;

	public UsuarioYaExisteException(String nombreUsuario) {
		super("El nombre de usuario " + nombreUsuario + " no se encuentra disponible");
	}

}
