package com.disneyapi.util.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.disneyapi.dto.genero.CrearGeneroDto;
import com.disneyapi.modelo.Genero;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GeneroDtoConverter {

	private final ModelMapper mapper;
	
	public Genero convertirCrearGeneroDtoAGenero(CrearGeneroDto generoDto) {
		return mapper.map(generoDto, Genero.class);
	}
}
