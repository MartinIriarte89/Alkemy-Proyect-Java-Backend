package com.disneyapi.controlador;

import java.util.Arrays;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.disneyapi.dto.GetUsuarioDto;
import com.disneyapi.dto.UsuarioLoginDto;
import com.disneyapi.dto.UsuarioRegistroDto;
import com.disneyapi.error.exception.ContrasenasNoCoincidenException;
import com.disneyapi.error.exception.UsuarioYaExisteException;
import com.disneyapi.error.exception.ValidacionException;
import com.disneyapi.modelo.Usuario;
import com.disneyapi.seguridad.JwtProveedor;
import com.disneyapi.servicio.EmailServicio;
import com.disneyapi.servicio.UsuarioServicio;
import com.disneyapi.util.converter.UsuarioDtoConverter;
import com.disneyapi.util.enumerados.RolUsuario;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticacionControlador {

	private final AuthenticationManager authenticationManager;
	private final JwtProveedor jwtProveedor;
	private final UsuarioServicio usuarioServicio;
	private final UsuarioDtoConverter converter;
	private final PasswordEncoder encriptador;
	private final EmailServicio emailServicio;

	@PostMapping("/login")
	public ResponseEntity<String> autenticarse(
			@Valid @RequestBody UsuarioLoginDto usuarioRegistroDto,
			final Errors errores) {
		if(errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				usuarioRegistroDto.getUsername(), usuarioRegistroDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwtToken = jwtProveedor.generarToken(usuarioRegistroDto.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED).body(jwtToken);
	}

	@PostMapping("/register")
	public ResponseEntity<GetUsuarioDto> registro(
			@Valid @RequestBody UsuarioRegistroDto usuarioRegistroDto,
			final Errors errores) {
		if(errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		
		if (!usuarioRegistroDto.getContrasena().equals(usuarioRegistroDto.getContrasenaRepetida())) {
			throw new ContrasenasNoCoincidenException();
		}
		if (usuarioServicio.existePorNombreUsuario(usuarioRegistroDto.getNombreUsuario())) {
			throw new UsuarioYaExisteException(usuarioRegistroDto.getNombreUsuario());
		}

		Usuario usuario = converter.convertirUsuarioRegistroDtoAUsuario(usuarioRegistroDto);
		usuario.setRoles(Arrays.asList(RolUsuario.ROLE_USER));
		usuario.setContrasena(encriptador.encode(usuario.getContrasena()));
		usuarioServicio.guardar(usuario);
		emailServicio.enviarMail(usuario.getNombreCompleto(), usuario.getEmail());

		return ResponseEntity.status(HttpStatus.CREATED).body(converter.convertirUsuarioAGetUsuarioDto(usuario));
	}
}
