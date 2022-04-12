package com.disneyapi.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.disneyapi.modelo.Personaje;

public interface PersonajeRepositorio extends JpaRepository<Personaje, Long>, JpaSpecificationExecutor<Personaje> {

}
