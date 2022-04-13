package com.disneyapi.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.disneyapi.modelo.Audiovisual;

public interface AudiovisualRepositorio
		extends JpaRepository<Audiovisual, Long>, JpaSpecificationExecutor<Audiovisual> {

	Optional<Audiovisual> findByTitulo(String titulo);
}
