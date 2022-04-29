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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

import com.disneyapi.dto.audiovisual.CrearAudiovisualDto;
import com.disneyapi.dto.audiovisual.EditarAudiovisualDto;
import com.disneyapi.dto.audiovisual.GetAudiovisualDto;
import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.Genero;
import com.disneyapi.modelo.Pelicula;
import com.disneyapi.modelo.Personaje;
import com.disneyapi.modelo.objetonulo.AudiovisualNulo;
import com.disneyapi.modelo.objetonulo.PersonajeNulo;
import com.disneyapi.seguridad.SeguridadConfig;
import com.disneyapi.servicio.AudiovisualServicio;
import com.disneyapi.servicio.PersonajeServicio;
import com.disneyapi.util.converter.AudiovisualDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

@WebMvcTest(value = AudiovisualControlador.class, excludeFilters = {
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
	
	ModelMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = new ModelMapper();
	}

	@Test
	void listarTodosTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, null, genero);
		Audiovisual audiovisual2 = new Pelicula(2L, null, "Prueba 2", LocalDate.now(), 3, null, genero);
		List<Audiovisual> lista = Arrays.asList(audiovisual, audiovisual2);
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
				.andExpect(jsonPath("$[1].titulo", is("Prueba 2")))
				.andExpect(jsonPath("$[0].fechaDeEstreno", is(LocalDate.now().toString())))
				.andExpect(jsonPath("$[1].fechaDeEstreno", is(LocalDate.now().toString())));
		
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
				.andExpect(jsonPath("$[0].titulo", is("Prueba")))
				.andExpect(jsonPath("$[0].fechaDeEstreno", is(LocalDate.now().toString())));
		
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
		Audiovisual audiovisualConImagen = new Pelicula(1L, "http://localhost:8080/files/miImagen.jpg", "Prueba", LocalDate.now(), 4, null, genero);
		
		String audiovisualJsonSerie = "{\"tipo\":\"serie\",\"titulo\":\"Prueba\",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4, \"personajesPersonajeId\":null, \"generoId\":1}";
		String audiovisualJsonPelicula = "{\"tipo\":\"pelicula\",\"titulo\":\"Prueba\",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4, \"personajesPersonajeId\":null, \"generoId\":1}";
		
		MockMultipartFile archivoJsonPelicula = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJsonPelicula.getBytes());
		MockMultipartFile archivoJsonSerie = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJsonSerie.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(converter.convertirCrearAudiovisualDtoAAudiovisual(any())).thenReturn(audiovisual);
		when(audiovisualServicio.guardarImagenYAgregarUrlImagen(eq(audiovisual), any())).thenReturn(audiovisualConImagen);
		when(audiovisualServicio.guardar(audiovisualConImagen)).thenReturn(audiovisualConImagen);
		
		mockMvc.perform(multipart("/movies").file(archivoJsonPelicula).file(archivoImagen))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("urlImagen", is("http://localhost:8080/files/miImagen.jpg")))
				.andExpect(jsonPath("titulo", is("Prueba")))
				.andExpect(jsonPath("calificacion", is(4.0)));
		
		mockMvc.perform(multipart("/movies").file(archivoJsonSerie).file(archivoImagen))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("urlImagen", is("http://localhost:8080/files/miImagen.jpg")))
				.andExpect(jsonPath("titulo", is("Prueba")))
				.andExpect(jsonPath("calificacion", is(4.0)));
		 
		verify(converter, times(2)).convertirCrearAudiovisualDtoAAudiovisual(any());
		verify(audiovisualServicio, times(2)).guardarImagenYAgregarUrlImagen(eq(audiovisual), any());
		verify(audiovisualServicio, times(2)).guardar(audiovisualConImagen);
	}	
	
	@Test
	void crearUnAudiovisualConErroresValidacionTest() throws Exception {
		String audiovisualJson = "{\"tipo\":\"pelicula\",\"titulo\":\"   \",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4, \"personajesPersonajeId\":null, \"generoId\":1}";
		MockMultipartFile archivoJson = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		mockMvc.perform(multipart("/movies").file(archivoJson).file(archivoImagen))
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
		String audiovisualJson = "{\"tipo\":\"serie\",\"titulo\":\"Prueba\",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4, \"personajesPersonajeId\":null, \"generoId\":1}";
		MockMultipartFile archivoJson = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(audiovisualServicio.existePorTitulo(audiovisualDto.getTitulo())).thenReturn(true);
		
		mockMvc.perform(multipart("/movies").file(archivoJson).file(archivoImagen))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("Prueba ya existe.")));
		
		verify(audiovisualServicio).existePorTitulo(audiovisualDto.getTitulo());
		verify(converter, never()).convertirCrearAudiovisualDtoAAudiovisual(any());
		verify(audiovisualServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(audiovisualServicio, never()).guardar(any());
	}
	
	@Test
	void crearUnAudiovisualTipoIncorrectoTest() throws Exception {
		String audiovisualJson = "{\"tipo\":\"comercial\",\"titulo\":\"Prueba\",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4, \"personajesPersonajeId\":null, \"generoId\":1}";
		MockMultipartFile archivoJson = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		mockMvc.perform(multipart("/movies").file(archivoJson).file(archivoImagen))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("El tipo comercial no es valido. Por favor seleccione pelicula o serie.")));
		
		verify(converter, never()).convertirCrearAudiovisualDtoAAudiovisual(any());
		verify(audiovisualServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(audiovisualServicio, never()).guardar(any());
	}
	
	@Test
	void crearUnAudiovisualSinPartesTest() throws Exception {		
		mockMvc.perform(multipart("/movies"))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		
		verify(converter, never()).convertirCrearAudiovisualDtoAAudiovisual(any());
		verify(audiovisualServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(audiovisualServicio, never()).guardar(any());
	}
	
	@Test
	void editarUnAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, null, genero);
		Audiovisual audiovisualConImagen = new Pelicula(1L, "http://localhost:8080/files/miImagen.jpg", "Prueba", LocalDate.now(), 4, null, genero);
		EditarAudiovisualDto audiovisualDto = new EditarAudiovisualDto("Prueba", LocalDate.now(), 4, 1L);
		String audiovisualJsonPelicula = "{\"titulo\":\"Prueba\",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4,\"generoId\":1}";
		
		MockMultipartFile archivoJsonPelicula = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJsonPelicula.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));
		when(converter.convertirEditarAudiovisualDtoAAudiovisual(audiovisualDto, audiovisual)).thenReturn(audiovisual);
		when(audiovisualServicio.guardarImagenYAgregarUrlImagen(eq(audiovisual), any())).thenReturn(audiovisualConImagen);
		when(audiovisualServicio.editar(audiovisualConImagen)).thenReturn(audiovisualConImagen);
		
		mockMvc.perform(multipart("/movies/1").file(archivoJsonPelicula).file(archivoImagen)
				.with( request -> {request.setMethod("PUT"); return request;}))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("urlImagen", is("http://localhost:8080/files/miImagen.jpg")))
					.andExpect(jsonPath("titulo", is("Prueba")))
					.andExpect(jsonPath("calificacion", is(4.0)));
		
		verify(audiovisualServicio).editar(audiovisualConImagen);
		verify(audiovisualServicio).guardarImagenYAgregarUrlImagen(eq(audiovisual), any());
		verify(converter).convertirEditarAudiovisualDtoAAudiovisual(audiovisualDto, audiovisual);
		verify(audiovisualServicio).buscarPorId(1L);
	}
	
	@Test
	void editarUnAudiovisualConErroresValidacionTest() throws Exception {
		String audiovisualJsonPelicula = "{\"titulo\":\"    \",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4,\"generoId\":1}";
		
		MockMultipartFile archivoJsonPelicula = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJsonPelicula.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		mockMvc.perform(multipart("/movies/1").file(archivoJsonPelicula).file(archivoImagen)
				.with( request -> {request.setMethod("PUT"); return request;}))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("mensaje", is("Existen errores de validacion")));
		
		verify(audiovisualServicio, never()).editar(any());
		verify(audiovisualServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(audiovisualServicio, never()).buscarPorId(1L);
	}
	
	@Test
	void editarUnAudiovisualInexistenteTest() throws Exception {
		String audiovisualJsonPelicula = "{\"titulo\":\"Prueba\",\"fechaDeEstreno\":\""+ LocalDate.now() + "\",\"calificacion\":4,\"generoId\":1}";
		
		MockMultipartFile archivoJsonPelicula = new MockMultipartFile("audiovisual", "audiovisual.json", "application/json", audiovisualJsonPelicula.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(AudiovisualNulo.contruir()));
		
		mockMvc.perform(multipart("/movies/1").file(archivoJsonPelicula).file(archivoImagen)
				.with( request -> {request.setMethod("PUT"); return request;}))
					.andExpect(status().isNotFound());
		
		verify(audiovisualServicio, never()).editar(any());
		verify(audiovisualServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(converter, never()).convertirEditarAudiovisualDtoAAudiovisual(any(), any());
		verify(audiovisualServicio).buscarPorId(1L);
	}
	
	@Test
	void editarUnAudiovisualSinPartesTest() throws Exception {
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(AudiovisualNulo.contruir()));
		
		mockMvc.perform(multipart("/movies/1").file(archivoImagen).with( request -> {request.setMethod("PUT"); return request;}))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		
		verify(audiovisualServicio, never()).editar(any());
		verify(audiovisualServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(converter, never()).convertirEditarAudiovisualDtoAAudiovisual(any(), any());
		verify(audiovisualServicio, never()).buscarPorId(1L);
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
	
	@Test
	void agregarPersonajeAAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, new ArrayList<>(), genero);
		Personaje personaje = new Personaje(1L, null, "Mickey Mouse", 22, 30, null, null);
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		when(audiovisualServicio.guardar(audiovisual)).thenReturn(audiovisual);
		
		mockMvc.perform(post("/movies/1/characters/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("titulo", is("Prueba")))
				.andExpect(jsonPath("calificacion", is(4.0)))
				.andExpect(jsonPath("personajes[0].nombre", is("Mickey Mouse")))
				.andExpect(jsonPath("personajes[0].edad", is(22)))
				.andExpect(jsonPath("personajes[0].peso", is(30.0)));
		
		verify(audiovisualServicio).buscarPorId(1L);
		verify(personajeServicio).buscarPorId(1L);
		verify(audiovisualServicio).guardar(audiovisual);
	}
	
	@Test
	void agregarPersonajeQueYaExisteEnAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Personaje personaje = new Personaje(1L, null, "Mickey Mouse", 22, 30, null, null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, Arrays.asList(personaje), genero);
		
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		when(audiovisualServicio.guardar(any())).thenReturn(audiovisual);
		
		mockMvc.perform(post("/movies/1/characters/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("El personaje Mickey Mouse ya se encontraba en la pelicula/serie")));
		
		verify(audiovisualServicio).buscarPorId(1L);
		verify(personajeServicio).buscarPorId(1L);
		verify(audiovisualServicio, never()).guardar(audiovisual);
	}
	
	@Test
	void agregarPersonajeInexistenteAAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, Arrays.asList(), genero);
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(PersonajeNulo.construir()));
		when(audiovisualServicio.guardar(audiovisual)).thenReturn(audiovisual);
		
		mockMvc.perform(post("/movies/1/characters/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		verify(audiovisualServicio).buscarPorId(1L);
		verify(personajeServicio).buscarPorId(1L);
		verify(audiovisualServicio, never()).guardar(any());
	}
	
	@Test
	void agregarPersonajeAAudiovisualInexistenteTest() throws Exception {
		Personaje personaje = new Personaje(1L, null, "Mickey Mouse", 22, 30, null, null);
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(AudiovisualNulo.contruir()));
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		when(audiovisualServicio.guardar(any())).thenReturn(any());
		
		mockMvc.perform(post("/movies/1/characters/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		verify(audiovisualServicio).buscarPorId(1L);
		verify(personajeServicio).buscarPorId(1L);
		verify(audiovisualServicio, never()).guardar(any());
	}
	
	@Test
	void eliminarPersonajeAAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Personaje personaje = new Personaje(1L, null, "Mickey Mouse", 22, 30, null, null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, new ArrayList<>(), genero);
		audiovisual.agregarA(personaje);
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		when(audiovisualServicio.guardar(audiovisual)).thenReturn(audiovisual);
		
		mockMvc.perform(delete("/movies/1/characters/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("titulo", is("Prueba")))
				.andExpect(jsonPath("calificacion", is(4.0)))
				.andExpect(jsonPath("$.personajes").isEmpty());
		
		verify(audiovisualServicio).buscarPorId(1L);
		verify(personajeServicio).buscarPorId(1L);
		verify(audiovisualServicio).guardar(audiovisual);
	}
	
	@Test
	void eliminarPersonajeQueNoTieneElAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Personaje personaje = new Personaje(1L, null, "Mickey Mouse", 22, 30, null, null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, new ArrayList<>(), genero);
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		when(audiovisualServicio.guardar(audiovisual)).thenReturn(audiovisual);
		
		mockMvc.perform(delete("/movies/1/characters/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("El personaje Mickey Mouse no se encuentra en la película/serie")));
		
		verify(audiovisualServicio).buscarPorId(1L);
		verify(personajeServicio).buscarPorId(1L);
		verify(audiovisualServicio, never()).guardar(audiovisual);
	}
	
	@Test
	void eliminarPersonajeInexistenteAAudiovisualTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, new ArrayList<>(), genero);
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(PersonajeNulo.construir()));
		when(audiovisualServicio.guardar(audiovisual)).thenReturn(audiovisual);
		
		mockMvc.perform(delete("/movies/1/characters/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		verify(audiovisualServicio).buscarPorId(1L);
		verify(personajeServicio).buscarPorId(1L);
		verify(audiovisualServicio, never()).guardar(audiovisual);
	}
	
	@Test
	void eliminarPersonajeAAudiovisualInexistenteTest() throws Exception {
		Personaje personaje = new Personaje(1L, null, "Mickey Mouse", 22, 30, null, null);
		
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(AudiovisualNulo.contruir()));
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		when(audiovisualServicio.guardar(any())).thenReturn(any());
		
		mockMvc.perform(delete("/movies/1/characters/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		verify(audiovisualServicio).buscarPorId(1L);
		verify(personajeServicio).buscarPorId(1L);
		verify(audiovisualServicio, never()).guardar(any());
	}
	
}
