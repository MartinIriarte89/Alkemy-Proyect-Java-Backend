package com.disneyapi.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.repositorio.AudiovisualRepositorio;
import com.disneyapi.servicio.base.BaseServicio;

@Service
public class AudiovisualServicio extends BaseServicio<Audiovisual, Long, AudiovisualRepositorio> {

	private AlmacenamientoServicio almacenamientoServicio;
	
	@Autowired
	public AudiovisualServicio(AudiovisualRepositorio repositorio, AlmacenamientoServicio almacenamientoServicio) {
		super(repositorio);
		this.almacenamientoServicio = almacenamientoServicio;
	}
}
