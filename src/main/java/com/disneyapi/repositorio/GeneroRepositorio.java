package com.disneyapi.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.disneyapi.modelo.Genero;

public interface GeneroRepositorio extends JpaRepository<Genero, Long> {

}
