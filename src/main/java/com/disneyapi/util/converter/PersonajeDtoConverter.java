package com.disneyapi.util.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.disneyapi.dto.CrearYEditarPersonajeDto;
import com.disneyapi.modelo.Personaje;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonajeDtoConverter {

	private final ModelMapper mapper;

	public Personaje convertirCrearYEditarPersonajeDtoAPersonaje(CrearYEditarPersonajeDto personajeDto) {
		return mapper.map(personajeDto, Personaje.class);
	}
}
