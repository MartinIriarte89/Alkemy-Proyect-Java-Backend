package com.disneyapi.util.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.disneyapi.dto.personaje.CrearYEditarPersonajeDto;
import com.disneyapi.dto.personaje.GetPersonajeDto;
import com.disneyapi.modelo.Personaje;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonajeDtoConverter {

	private final ModelMapper mapper;

	public Personaje convertirCrearYEditarPersonajeDtoAPersonaje(CrearYEditarPersonajeDto personajeDto) {
		return mapper.map(personajeDto, Personaje.class);
	}

	public Personaje convertirCrearYEditarPersonajeDtoAPersonaje(CrearYEditarPersonajeDto personajeDto,
			Personaje personaje) {
		mapper.map(personajeDto, personaje);
		return personaje;
	}

	public GetPersonajeDto convertirPersonajeAGetPersonajeDto(Personaje personaje) {
		return mapper.map(personaje, GetPersonajeDto.class);
	}
}
