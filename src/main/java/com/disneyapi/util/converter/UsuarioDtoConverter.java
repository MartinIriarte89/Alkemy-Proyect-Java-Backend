package com.disneyapi.util.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.disneyapi.dto.UsuarioRegistroDto;
import com.disneyapi.dto.usuario.GetUsuarioDto;
import com.disneyapi.modelo.Usuario;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioDtoConverter {

	private final ModelMapper mapper;

	public Usuario convertirUsuarioRegistroDtoAUsuario(UsuarioRegistroDto usuarioDto) {
		return mapper.map(usuarioDto, Usuario.class);
	}
	
	public GetUsuarioDto convertirUsuarioAGetUsuarioDto(Usuario usuario) {
		return mapper.map(usuario, GetUsuarioDto.class);
	}
}
