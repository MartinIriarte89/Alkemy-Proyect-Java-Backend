package com.disneyapi.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.disneyapi.modelo.Personaje;
import com.disneyapi.repositorio.PersonajeRepositorio;
import com.disneyapi.servicio.base.BaseServicio;

@Service
public class PersonajeServicio extends BaseServicio<Personaje, Long, PersonajeRepositorio> {

	private AlmacenamientoServicio almacenamientoServicio;

	@Autowired
	public PersonajeServicio(PersonajeRepositorio repositorio, AlmacenamientoServicio almacenamientoServicio) {
		super(repositorio);
		this.almacenamientoServicio = almacenamientoServicio;
	}
}
