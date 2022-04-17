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
import com.disneyapi.error.ApiError;
import com.disneyapi.error.exception.ContrasenasNoCoincidenException;
import com.disneyapi.error.exception.ErrorAlEnviarEmailRegistro;
import com.disneyapi.error.exception.UsuarioYaExisteException;
import com.disneyapi.error.exception.ValidacionException;
import com.disneyapi.modelo.Usuario;
import com.disneyapi.seguridad.JwtProveedor;
import com.disneyapi.servicio.EmailServicio;
import com.disneyapi.servicio.UsuarioServicio;
import com.disneyapi.util.converter.UsuarioDtoConverter;
import com.disneyapi.util.enumerados.RolUsuario;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

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
	
	@ApiOperation(value = "Loguearse", 
			notes = "Provee un mecanismo para loguearse con un nombre de usuario y contraseña."
					+ " Retorna un JWT en forma de cadena.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Created", response = Object.class),
			@ApiResponse(code = 201, message = "Created", response = String.class),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 409, message = "Conflict", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@PostMapping("/login")
	public ResponseEntity<String> autenticarse(
			@ApiParam(value = "Representacion Json del usuario y contraseña necesarios para el logueo", required = true, type = "Json")
			@Valid @RequestBody UsuarioLoginDto usuarioRegistroDto,
			@ApiIgnore final Errors errores) {
		if(errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				usuarioRegistroDto.getUsername(), usuarioRegistroDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwtToken = jwtProveedor.generarToken(usuarioRegistroDto.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED).body(jwtToken);
	}

	@ApiOperation(value = "Registrarse.", 
			notes = "Provee un mecanismo para registrarse. Como parte del registro exitoso tambien "
					+ " se envia en el cuerpo el jwt para no necesitar loguearse.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Created", response = Object.class),
			@ApiResponse(code = 201, message = "Created", response = GetUsuarioDto.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 409, message = "Conflict", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@PostMapping("/register")
	public ResponseEntity<GetUsuarioDto> registro(
			@ApiParam(value = "Representacion Json de un Usuario, necesario para llevar a cabo el registro", required = true, type = "Json")
			@Valid @RequestBody UsuarioRegistroDto usuarioRegistroDto,
			@ApiIgnore final Errors errores) {
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
		if(!emailServicio.enviarMail(usuario.getNombreCompleto(), usuario.getEmail())){
			throw new ErrorAlEnviarEmailRegistro();
		}
		GetUsuarioDto usuarioDto = converter.convertirUsuarioAGetUsuarioDto(usuario);
		usuarioDto.setToken(jwtProveedor.generarToken(usuarioDto.getNombreUsuario()));

		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDto);
	}
}
