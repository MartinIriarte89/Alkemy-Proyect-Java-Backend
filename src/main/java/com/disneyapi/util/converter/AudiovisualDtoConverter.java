package com.disneyapi.util.converter;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.disneyapi.dto.CrearAudiovisualDto;
import com.disneyapi.dto.EditarAudiovisualDto;
import com.disneyapi.dto.GetAudiovisualDto;
import com.disneyapi.error.exception.GeneroNoExisteException;
import com.disneyapi.error.exception.PersonajesNoExistenException;
import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.Pelicula;
import com.disneyapi.modelo.Serie;
import com.disneyapi.servicio.GeneroServicio;
import com.disneyapi.servicio.PersonajeServicio;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AudiovisualDtoConverter {

	private final ModelMapper mapper;
	private final GeneroServicio generoServicio;
	private final PersonajeServicio personajeServicio;

	public GetAudiovisualDto convertirAudiovisualAGetAudiovisualDto(Audiovisual audiovisual) {
		return mapper.map(audiovisual, GetAudiovisualDto.class);
	}

	public Audiovisual convertirCrearAudiovisualDtoAAudiovisual(CrearAudiovisualDto audiovisualDto) {
		try {
			if (audiovisualDto.getTipo().equalsIgnoreCase("pelicual")) {
				return mapper.map(audiovisualDto, Pelicula.class);
			} else {
				return mapper.map(audiovisualDto, Serie.class);
			}
		} catch (EntityNotFoundException e) {
			if (generoServicio.existePorId(audiovisualDto.getGeneroId())) {
				throw new GeneroNoExisteException(audiovisualDto.getGeneroId());
			} else {
				audiovisualDto.getPersonajesPersonajeId().removeIf(p -> personajeServicio.existePorId(p.getId()));
				throw new PersonajesNoExistenException(audiovisualDto.getPersonajesPersonajeId());
			}
		}
	}

	public Audiovisual convertirEditarAudiovisualDtoAAudiovisual(EditarAudiovisualDto audiovisualDto,
			Audiovisual audiovisual) {
		mapper.map(audiovisualDto, audiovisual);
		return audiovisual;
	}
}
