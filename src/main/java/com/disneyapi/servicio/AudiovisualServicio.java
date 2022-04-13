package com.disneyapi.servicio;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	public Optional<Audiovisual> buscarPorTitulo(String titulo) {
		return this.repositorio.findByTitulo(titulo);
	}

	public Page<Audiovisual> buscarPorArgs(Optional<String> genero, Optional<String> orden, Pageable pageable) {
		return null;
	}
}
