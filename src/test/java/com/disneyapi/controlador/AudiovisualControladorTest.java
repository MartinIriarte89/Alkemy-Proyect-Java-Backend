package com.disneyapi.controlador;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.Genero;
import com.disneyapi.modelo.Pelicula;
import com.disneyapi.seguridad.JwtProveedor;
import com.disneyapi.servicio.AlmacenamientoServicio;
import com.disneyapi.servicio.AudiovisualServicio;
import com.disneyapi.servicio.GeneroServicio;
import com.disneyapi.servicio.PersonajeServicio;
import com.disneyapi.servicio.UserDetailsServiceImpl;
import com.disneyapi.servicio.UsuarioServicio;
import com.disneyapi.util.converter.AudiovisualDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

@WebMvcTest(value =  AudiovisualControlador.class)
public class AudiovisualControladorTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AudiovisualServicio audiovisualServicio;
	@MockBean
	private PersonajeServicio personajeServicio;
	@MockBean
	private AudiovisualDtoConverter converter;
	@MockBean
	private PaginacionLinks links;
	@MockBean
	private UserDetailsServiceImpl detailsServiceImpl;
	@MockBean
	private JwtProveedor jwtProveedor;
	@MockBean
	private PasswordEncoder encoder;
	@MockBean
	private GeneroServicio generoServicio;
	@MockBean
	private UsuarioServicio usuarioServicio;
	@MockBean
	private AlmacenamientoServicio almacenamientoServicio;
	
	
	@Test
	void testListarAudiovisuales() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, null, genero);
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));
		
		mockMvc.perform(get("/movies/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpectAll(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.titulo").value("Prueba"))
				.andExpect(jsonPath("$.calificacion").value(4));
	}
}
