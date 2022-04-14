package com.disneyapi.controlador;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.disneyapi.seguridad.JwtProveedor;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticacionControlador {
	
	private final AuthenticationManager authenticationManager;
	private final JwtProveedor jwtProveedor;

	@PostMapping("/login")
	public ResponseEntity<String> autenticarse(@Valid @RequestBody UsuarioRegistroDto usuarioRegistroDto) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				usuarioRegistroDto.getUsername(), usuarioRegistroDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwtToken = jwtProveedor.generarToken(usuarioRegistroDto.getUsername);
		
		ResponseEntity.status(HttpStatus.CREATED).body(jwtToken);
	}
}
