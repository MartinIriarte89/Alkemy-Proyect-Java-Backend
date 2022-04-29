package com.disneyapi.controlador;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.disneyapi.dto.UsuarioLoginDto;
import com.disneyapi.dto.UsuarioRegistroDto;
import com.disneyapi.dto.usuario.GetUsuarioDto;
import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.modelo.Usuario;
import com.disneyapi.seguridad.JwtProveedor;
import com.disneyapi.seguridad.SeguridadConfig;
import com.disneyapi.servicio.EmailServicio;
import com.disneyapi.servicio.UsuarioServicio;
import com.disneyapi.util.converter.UsuarioDtoConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@WebMvcTest(value = AutenticacionControlador.class, excludeFilters = {
    	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
    	classes = { SeguridadConfig.class, AutorizacionFiltro.class})},  
		excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AutenticacionControladorTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AuthenticationManager authenticationManager;
	@MockBean
	private Authentication authentication;
	@MockBean
	private JwtProveedor jwtProveedor;
	@MockBean
	private UsuarioServicio usuarioServicio;
	@MockBean
	private UsuarioDtoConverter converter;
	@MockBean
	private PasswordEncoder encoder;
	@MockBean
	private EmailServicio emailServicio;
	
	ObjectMapper objectMapper;
	ObjectWriter objectWriter;
	
	@BeforeEach
	void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
	} 
	
	@Test
	void autenticarseTest() throws Exception {
		UsuarioLoginDto usuarioLoginDto = new UsuarioLoginDto("JuanAlfon", "123456");
		String usuarioLoginJson = objectWriter.writeValueAsString(usuarioLoginDto);
		
		when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				usuarioLoginDto.getUsername(), usuarioLoginDto.getPassword()))).thenReturn(authentication);
		when(jwtProveedor.generarToken(usuarioLoginDto.getUsername())).thenReturn("Token Nuevo");
		
		mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(usuarioLoginJson))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE.concat(";charset=UTF-8")))
				.andExpect(content().string(is("Token Nuevo")));
		
		verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(
				usuarioLoginDto.getUsername(), usuarioLoginDto.getPassword()));
		verify(jwtProveedor).generarToken(usuarioLoginDto.getUsername());
	}
	
	@Test
	void autenticarseConErroresValidacionTest() throws Exception {
		UsuarioLoginDto usuarioLoginDto = new UsuarioLoginDto("    ", "123456");
		String usuarioLoginJson = objectWriter.writeValueAsString(usuarioLoginDto);
		
		mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(usuarioLoginJson))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("Existen errores de validacion")));
		
		verify(authenticationManager, never()).authenticate(new UsernamePasswordAuthenticationToken(
				usuarioLoginDto.getUsername(), usuarioLoginDto.getPassword()));
		verify(jwtProveedor, never()).generarToken(usuarioLoginDto.getUsername());
	}
	
	@Test
	void registroTest() throws Exception {
		Usuario usuario = new Usuario(1L, "Juan Alfonsin", "Juanalfon", "123456", "jalfon@gmail.com", null);
		UsuarioRegistroDto registroDto = new UsuarioRegistroDto("Juan Alfonsin", "Juanalfon", "123456", "123456", "jalfon@gmail.com");
		GetUsuarioDto usuarioDto = new GetUsuarioDto("Juan Alfonsin", "Juanalfon", "jalfon@gmail.com", null);
		String usuarioRegistroDto = objectWriter.writeValueAsString(registroDto);
		
		when(converter.convertirUsuarioRegistroDtoAUsuario(registroDto)).thenReturn(usuario);
		when(emailServicio.enviarMail(usuario.getNombreCompleto(), usuario.getEmail())).thenReturn(true);
		when(converter.convertirUsuarioAGetUsuarioDto(usuario)).thenReturn(usuarioDto);
		when(jwtProveedor.generarToken(usuarioDto.getNombreUsuario())).thenReturn("token nuevo");
		
		mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(usuarioRegistroDto))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("nombreCompleto", is("Juan Alfonsin")))
				.andExpect(jsonPath("nombreUsuario", is("Juanalfon")))
				.andExpect(jsonPath("email", is("jalfon@gmail.com")))
				.andExpect(jsonPath("token", is("token nuevo")));
		
		verify(converter).convertirUsuarioRegistroDtoAUsuario(registroDto);
		verify(converter).convertirUsuarioAGetUsuarioDto(usuario);
		verify(emailServicio).enviarMail(usuario.getNombreCompleto(), usuario.getEmail());
		verify(jwtProveedor).generarToken(usuarioDto.getNombreUsuario());
		verify(usuarioServicio).guardar(usuario);
	}
	
	@Test
	void registroConErroresValidacionTest() throws Exception {
		UsuarioRegistroDto registroDto = new UsuarioRegistroDto("      ", "Juanalfon", "123456", "123456", "jalfon@gmail.com");
		String usuarioRegistroDto = objectWriter.writeValueAsString(registroDto);
		
		mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(usuarioRegistroDto))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("Existen errores de validacion")));
		
		verify(converter, never()).convertirUsuarioRegistroDtoAUsuario(registroDto);
		verify(converter, never()).convertirUsuarioAGetUsuarioDto(any());
		verify(emailServicio, never()).enviarMail(anyString(), anyString());
		verify(jwtProveedor, never()).generarToken(anyString());
		verify(usuarioServicio, never()).guardar(any());
	}
	
	@Test
	void registroConContrasenasDiferentesTest() throws Exception {
		UsuarioRegistroDto registroDto = new UsuarioRegistroDto("Juan Alfonsin", "Juanalfon", "123456789", "1239999", "jalfon@gmail.com");
		String usuarioRegistroDto = objectWriter.writeValueAsString(registroDto);
		
		mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(usuarioRegistroDto))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("Las contraseñas deben ser idénticas")));
		
		verify(converter, never()).convertirUsuarioRegistroDtoAUsuario(registroDto);
		verify(converter, never()).convertirUsuarioAGetUsuarioDto(any());
		verify(emailServicio, never()).enviarMail(anyString(), anyString());
		verify(jwtProveedor, never()).generarToken(anyString());
		verify(usuarioServicio, never()).guardar(any());
	}
	
	@Test
	void registroUsuarioYaExisteTest() throws Exception {
		UsuarioRegistroDto registroDto = new UsuarioRegistroDto("Juan Alfonsin", "Juanalfon", "123456789", "123456789", "jalfon@gmail.com");
		String usuarioRegistroDto = objectWriter.writeValueAsString(registroDto);
		
		when(usuarioServicio.existePorNombreUsuario(registroDto.getNombreUsuario())).thenReturn(true);
		
		mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(usuarioRegistroDto))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("El nombre de usuario Juanalfon no se encuentra disponible")));
		
		verify(usuarioServicio).existePorNombreUsuario(registroDto.getNombreUsuario());
		verify(converter, never()).convertirUsuarioRegistroDtoAUsuario(registroDto);
		verify(converter, never()).convertirUsuarioAGetUsuarioDto(any());
		verify(emailServicio, never()).enviarMail(anyString(), anyString());
		verify(jwtProveedor, never()).generarToken(anyString());
		verify(usuarioServicio, never()).guardar(any());
	}
	
	@Test
	void registroErrorAlEnviarMailTest() throws Exception {
		Usuario usuario = new Usuario(1L, "Juan Alfonsin", "Juanalfon", "123456", "jalfon@gmail.com", null);
		UsuarioRegistroDto registroDto = new UsuarioRegistroDto("Juan Alfonsin", "Juanalfon", "123456", "123456", "jalfon@gmail.com");
		GetUsuarioDto usuarioDto = new GetUsuarioDto("Juan Alfonsin", "Juanalfon", "jalfon@gmail.com", null);
		String usuarioRegistroDto = objectWriter.writeValueAsString(registroDto);
		
		when(converter.convertirUsuarioRegistroDtoAUsuario(registroDto)).thenReturn(usuario);
		when(emailServicio.enviarMail(usuario.getNombreCompleto(), usuario.getEmail())).thenReturn(false);
		when(converter.convertirUsuarioAGetUsuarioDto(usuario)).thenReturn(usuarioDto);
		when(jwtProveedor.generarToken(usuarioDto.getNombreUsuario())).thenReturn("token nuevo");
		
		mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(usuarioRegistroDto))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("Ha ocurrido un error al querer enviar el mail a su casilla de correo")));
		
		verify(converter).convertirUsuarioRegistroDtoAUsuario(registroDto);
		verify(converter, never()).convertirUsuarioAGetUsuarioDto(usuario);
		verify(emailServicio).enviarMail(usuario.getNombreCompleto(), usuario.getEmail());
		verify(jwtProveedor, never()).generarToken(usuarioDto.getNombreUsuario());
		verify(usuarioServicio, never()).guardar(usuario);
	}
}
