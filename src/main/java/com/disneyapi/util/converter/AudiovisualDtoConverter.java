package com.disneyapi.util.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.disneyapi.dto.CrearYEditarAudiovisualDto;
import com.disneyapi.dto.GetAudiovisualDto;
import com.disneyapi.modelo.Audiovisual;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AudiovisualDtoConverter {

	private final ModelMapper mapper;

	public GetAudiovisualDto convertirAudiovisualAGetAudiovisualDto(Audiovisual audiovisual) {
		return mapper.map(audiovisual, GetAudiovisualDto.class);
	}

	public Audiovisual convertirCrearYEditarAudiovisualDtoAAudiovisual(CrearYEditarAudiovisualDto audiovisualDto) {
		return mapper.map(audiovisualDto, Audiovisual.class);
	}
}
