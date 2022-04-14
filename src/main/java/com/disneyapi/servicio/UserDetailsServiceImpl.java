package com.disneyapi.servicio;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.disneyapi.modelo.Usuario;

import lombok.RequiredArgsConstructor;

@Service("UserDetailsService")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UsuarioServicio usuarioServicio;

	@Override
	public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
		Usuario usuario = usuarioServicio.buscarPorNombreUsuario(nombreUsuario)
				.orElseThrow(() -> new UsernameNotFoundException(nombreUsuario + " no encontrado."));

		Collection<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
				.map(rol -> new SimpleGrantedAuthority(rol.name())).collect(Collectors.toList());

		return new User(usuario.getNombreUsuario(), usuario.getContrasena(), authorities);
	}
}
