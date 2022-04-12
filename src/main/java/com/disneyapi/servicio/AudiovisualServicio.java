package com.disneyapi.servicio;

import org.springframework.stereotype.Service;

import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.repositorio.AudiovisualRepositorio;
import com.disneyapi.servicio.base.BaseServicio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AudiovisualServicio extends BaseServicio<Audiovisual, Long, AudiovisualRepositorio> {

	private final AlmacenamientoServicio almacenamientoServicio;
	
	public AudiovisualServicio(AudiovisualRepositorio repositorio, AlmacenamientoServicio almacenamientoServicio) {
		super(repositorio);
		this.almacenamientoServicio = almacenamientoServicio;
	}
}
