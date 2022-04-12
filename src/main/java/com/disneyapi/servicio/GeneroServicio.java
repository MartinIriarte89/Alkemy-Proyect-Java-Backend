package com.disneyapi.servicio;

import org.springframework.stereotype.Service;

import com.disneyapi.modelo.Genero;
import com.disneyapi.repositorio.GeneroRepositorio;
import com.disneyapi.servicio.base.BaseServicio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeneroServicio extends BaseServicio<Genero, Long, GeneroRepositorio> {

	private final AlmacenamientoServicio almacenamientoServicio;
	
	public GeneroServicio(GeneroRepositorio repositorio, AlmacenamientoServicio almacenamientoServicio) {
		super(repositorio);
		this.almacenamientoServicio = almacenamientoServicio;
	}

}
