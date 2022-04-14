package com.disneyapi.servicio;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.disneyapi.modelo.Usuario;
import com.disneyapi.repositorio.UsuarioRepositorio;
import com.disneyapi.servicio.base.BaseServicio;

@Service
public class UsuarioServicio extends BaseServicio<Usuario, Long, UsuarioRepositorio> {

	public UsuarioServicio(UsuarioRepositorio repositorio) {
		super(repositorio);
	}

	public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario){
		return this.repositorio.findByNombreUsuario(nombreUsuario);
	}
}
