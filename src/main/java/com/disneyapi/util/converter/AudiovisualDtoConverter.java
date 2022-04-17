package com.disneyapi.util.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.disneyapi.dto.CrearYEditarAudiovisualDto;
import com.disneyapi.dto.GetAudiovisualDto;
import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.Pelicula;
import com.disneyapi.modelo.Serie;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AudiovisualDtoConverter {

	private final ModelMapper mapper;

	public GetAudiovisualDto convertirAudiovisualAGetAudiovisualDto(Audiovisual audiovisual) {
		return mapper.map(audiovisual, GetAudiovisualDto.class);
	}

	public Audiovisual convertirCrearYEditarAudiovisualDtoAAudiovisual(CrearYEditarAudiovisualDto audiovisualDto) {
		if(audiovisualDto.getTipo().equalsIgnoreCase("pelicual")) {
			return mapper.map(audiovisualDto, Pelicula.class);
		}else {
			return mapper.map(audiovisualDto, Serie.class);
		}
	}
}
