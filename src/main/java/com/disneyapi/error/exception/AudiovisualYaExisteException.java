package com.disneyapi.error.exception;

public class AudiovisualYaExisteException extends RuntimeException{

	private static final long serialVersionUID = -3460071270959430753L;

	public AudiovisualYaExisteException(String nombreAudiovisual) {
		 super(nombreAudiovisual + " ya existe.");
	 }
}
