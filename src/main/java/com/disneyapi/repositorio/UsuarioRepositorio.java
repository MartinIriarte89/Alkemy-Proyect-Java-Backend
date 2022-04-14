package com.disneyapi.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.disneyapi.modelo.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long>{

	Optional<Usuario> findByNombreUsuario(String nombreUsuario);

}
