package com.disneyapi.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.disneyapi.modelo.Personaje;

public interface PersonajeRepositorio extends JpaRepository<Personaje, Long>, JpaSpecificationExecutor<Personaje> {

	Optional<Personaje> findByNombre(String string);

	Optional<Personaje> findByNombreIgnoreCase (String string);

	boolean existsByNombreIgnoreCase(String nombre);

}
