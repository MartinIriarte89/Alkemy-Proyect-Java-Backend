package com.disneyapi.util.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.disneyapi.dto.audiovisual.CrearAudiovisualDto;
import com.disneyapi.dto.audiovisual.EditarAudiovisualDto;
import com.disneyapi.dto.audiovisual.GetAudiovisualDto;
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

	public Audiovisual convertirCrearAudiovisualDtoAAudiovisual(CrearAudiovisualDto audiovisualDto) {
		if (audiovisualDto.getTipo().equalsIgnoreCase("pelicula"))
			return mapper.map(audiovisualDto, Pelicula.class);
		else
			return mapper.map(audiovisualDto, Serie.class);
	}

	public Audiovisual convertirEditarAudiovisualDtoAAudiovisual(EditarAudiovisualDto audiovisualDto,
			Audiovisual audiovisual) {
		mapper.map(audiovisualDto, audiovisual);
		return audiovisual;
	}
}
