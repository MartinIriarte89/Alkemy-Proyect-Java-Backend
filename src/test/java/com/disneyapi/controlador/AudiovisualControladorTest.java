package com.disneyapi.controlador;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.disneyapi.dto.audiovisual.CrearAudiovisualDto;
import com.disneyapi.dto.audiovisual.GetAudiovisualDto;
import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.Genero;
import com.disneyapi.modelo.Pelicula;
import com.disneyapi.seguridad.SeguridadConfig;
import com.disneyapi.servicio.AudiovisualServicio;
import com.disneyapi.servicio.PersonajeServicio;
import com.disneyapi.util.converter.AudiovisualDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

@WebMvcTest(value = AudiovisualControlador.class,  excludeFilters = {
        	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
        	classes = { SeguridadConfig.class, AutorizacionFiltro.class})},  
			excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AudiovisualControladorTest {

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
	
	ModelMapper mapper = new ModelMapper();

	@Test
	void listarTodosTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, null, genero);
		Audiovisual audiovisual2 = new Pelicula(2L, null, "Prueba 2", LocalDate.now(), 3, null, genero);
		List<Audiovisual> lista = new ArrayList<>();
		lista.add(audiovisual);
		lista.add(audiovisual2);
		Page<Audiovisual> audiovisuales = new PageImpl<Audiovisual>(lista);
		
		when(audiovisualServicio.buscarPorArgs(any(), any(), any())).thenReturn(audiovisuales);
		when(converter.convertirAudiovisualAGetAudiovisualDto(audiovisual))
						.thenReturn(mapper.map(audiovisual, GetAudiovisualDto.class));
		when(converter.convertirAudiovisualAGetAudiovisualDto(audiovisual2))
						.thenReturn(mapper.map(audiovisual2, GetAudiovisualDto.class));
		
		mockMvc.perform(get("/movies"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].titulo", is("Prueba")))
				.andExpect(jsonPath("$[1].titulo", is("Prueba 2")));
		
		verify(converter, times(2)).convertirAudiovisualAGetAudiovisualDto(any());
		verify(audiovisualServicio).buscarPorArgs(any(), any(), any());
	}
	
	@Test
	void listarTodosNoEncontradosTest() throws Exception {
		when(audiovisualServicio.buscarPorArgs(any(), any(), any())).thenReturn(Page.empty());
		
		mockMvc.perform(get("/movies"))
				.andExpect(status().isNotFound());
		
		verify(audiovisualServicio).buscarPorArgs(any(), any(), any());
	}
	
	@Test
	void listarConNombreEnPathTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, null, genero);
		List<Audiovisual> lista = new ArrayList<>();
		lista.add(audiovisual);
		
		when(audiovisualServicio.buscarPorTituloIgnoreCase("Prueba")).thenReturn(Optional.of(audiovisual));
		when(converter.convertirAudiovisualAGetAudiovisualDto(audiovisual))
						.thenReturn(mapper.map(audiovisual, GetAudiovisualDto.class));
		
		mockMvc.perform(get("/movies?name=Prueba"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].titulo", is("Prueba")));
		
		verify(converter).convertirAudiovisualAGetAudiovisualDto(any());
		verify(audiovisualServicio).buscarPorTituloIgnoreCase("Prueba");
	}
	
	@Test
	void listarConNombreEnPathNoExisteTest() throws Exception {
		when(audiovisualServicio.buscarPorTituloIgnoreCase("Prueba")).thenReturn(Optional.empty());
		
		mockMvc.perform(get("/movies?name=Prueba"))
				.andExpect(status().isNotFound());
		
		verify(audiovisualServicio).buscarPorTituloIgnoreCase("Prueba");
	}

	@Test
	void buscarUnAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, null, genero);
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));

		mockMvc.perform(get("/movies/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.titulo").value("Prueba"))
				.andExpect(jsonPath("$.calificacion").value(4));

		verify(audiovisualServicio).buscarPorId(1L);
	}

	@Test
	void buscarUnAudiovisualNoEncontradoTest() throws Exception {
		when(audiovisualServicio.buscarPorId(anyLong())).thenReturn(Optional.empty());

		mockMvc.perform(get("/movies/2").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(audiovisualServicio).buscarPorId(2L);
	}
	
	@Test
	void crearUnAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, null, genero);
		CrearAudiovisualDto audiovisualDto = new CrearAudiovisualDto("pelicula", "Prueba", LocalDate.now(), 4, null, 1);
		Audiovisual audiovisualConImagen = new Pelicula(1L, "http://localhost:8080/files/miImagen.jpg", "Prueba", LocalDate.now(), 4, null, genero);
		String audiovisualJson = "{\"tipo\":\"pelicula\",\"titulo\":\"Prueba\",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4, \"personajesPersonajeId\":null, \"generoId\":1}";
		MockMultipartFile archivoJson = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(converter.convertirCrearAudiovisualDtoAAudiovisual(audiovisualDto)).thenReturn(audiovisual);
		when(audiovisualServicio.guardarImagenYAgregarUrlImagen(eq(audiovisual), any())).thenReturn(audiovisualConImagen);
		when(audiovisualServicio.guardar(audiovisualConImagen)).thenReturn(audiovisualConImagen);
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/movies").file(archivoJson).file(archivoImagen))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("urlImagen", is("http://localhost:8080/files/miImagen.jpg")))
				.andExpect(jsonPath("titulo", is("Prueba")))
				.andExpect(jsonPath("calificacion", is(4.0)));
		 
		verify(converter).convertirCrearAudiovisualDtoAAudiovisual(audiovisualDto);
		verify(audiovisualServicio).guardarImagenYAgregarUrlImagen(eq(audiovisual), any());
		verify(audiovisualServicio).guardar(audiovisualConImagen);
	}
	
	@Test
	void crearUnAudiovisualConErroresValidacionTest() throws Exception {
		String audiovisualJson = "{\"tipo\":\"pelicula\",\"titulo\":\"   \",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4, \"personajesPersonajeId\":null, \"generoId\":1}";
		MockMultipartFile archivoJson = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/movies").file(archivoJson).file(archivoImagen))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("Existen errores de validacion")));
		
		verify(converter, never()).convertirCrearAudiovisualDtoAAudiovisual(any());
		verify(audiovisualServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(audiovisualServicio, never()).guardar(any());
	}
	
	@Test
	void crearUnAudiovisualQueYaExisteTest() throws Exception {
		CrearAudiovisualDto audiovisualDto = new CrearAudiovisualDto("pelicula", "Prueba", LocalDate.now(), 4, null, 1);
		String audiovisualJson = "{\"tipo\":\"pelicula\",\"titulo\":\"Prueba\",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4, \"personajesPersonajeId\":null, \"generoId\":1}";
		MockMultipartFile archivoJson = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(audiovisualServicio.existePorTitulo(audiovisualDto.getTitulo())).thenReturn(true);
		
		mockMvc.perform(MockMvcRequestBuilders.multipart("/movies").file(archivoJson).file(archivoImagen))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("Prueba ya existe.")));
		
		verify(audiovisualServicio).existePorTitulo(audiovisualDto.getTitulo());
		verify(converter, never()).convertirCrearAudiovisualDtoAAudiovisual(any());
		verify(audiovisualServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(audiovisualServicio, never()).guardar(any());
	}
	
	@Test
	void borrarUnAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, null, genero);
		
		when(audiovisualServicio.buscarPorId(anyLong())).thenReturn(Optional.of(audiovisual));

		mockMvc.perform(delete("/movies/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(audiovisualServicio).buscarPorId(anyLong());
	}
	
	@Test
	void borrarUnAudiovisualNoEncontradoTest() throws Exception {
		when(audiovisualServicio.buscarPorId(anyLong())).thenReturn(Optional.empty());

		mockMvc.perform(delete("/movies/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(audiovisualServicio).buscarPorId(anyLong());
	}
}
