package com.disneyapi.error.exception;

import java.util.List;

import com.disneyapi.dto.CrearAudiovisualDto.PersonajeIdDto;

public class PersonajesNoExistenException extends RuntimeException {

	private static final long serialVersionUID = -4430930995394326098L;

	public PersonajesNoExistenException(List<PersonajeIdDto> personajesPersonajeId) {
		super("El o los personajes con id: " + personajesPersonajeId.toString() + "no existen.");
	}
}
