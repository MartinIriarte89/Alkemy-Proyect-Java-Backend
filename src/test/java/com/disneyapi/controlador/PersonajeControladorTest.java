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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import com.disneyapi.dto.personaje.CrearYEditarPersonajeDto;
import com.disneyapi.dto.personaje.GetPersonajeDto;
import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.Genero;
import com.disneyapi.modelo.Pelicula;
import com.disneyapi.modelo.Personaje;
import com.disneyapi.modelo.objetonulo.PersonajeNulo;
import com.disneyapi.seguridad.SeguridadConfig;
import com.disneyapi.servicio.AudiovisualServicio;
import com.disneyapi.servicio.PersonajeServicio;
import com.disneyapi.util.converter.PersonajeDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

@WebMvcTest(value = PersonajeControlador.class,  excludeFilters = {
    	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
    	classes = { SeguridadConfig.class, AutorizacionFiltro.class})},  
		excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class PersonajeControladorTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private PersonajeServicio personajeServicio;
	@MockBean
	private PersonajeDtoConverter converter;
	@MockBean
	private PaginacionLinks links;
	@MockBean
	private AudiovisualServicio audiovisualServicio;
	
	ModelMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = new ModelMapper();
	}
	
	@Test
	void listarPersonajesTest() throws Exception {
		Personaje personaje = new Personaje(1L, "http://localhost:8080/files/Mickey.jpg", "Mickey Mouse", 22, 30, null, null);
		Personaje personaje2 = new Personaje(2L, "http://localhost:8080/files/Donald.jpg", "Pato Donald", 24, 36, null, null);
		List<Personaje> lista = Arrays.asList(personaje, personaje2);
		Page<Personaje> personajes = new PageImpl<>(lista);
		
		when(personajeServicio.buscarPorArgs(any(),any(),any(),any())).thenReturn(personajes);
		when(converter.convertirPersonajeAGetPersonajeDto(personaje))
						.thenReturn(mapper.map(personaje, GetPersonajeDto.class));
		when(converter.convertirPersonajeAGetPersonajeDto(personaje2))
						.thenReturn(mapper.map(personaje2, GetPersonajeDto.class));
		
		mockMvc.perform(get("/characters"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].nombre", is("Mickey Mouse")))
				.andExpect(jsonPath("$[1].nombre", is("Pato Donald")))
				.andExpect(jsonPath("$[0].urlImagen", is("http://localhost:8080/files/Mickey.jpg")))
				.andExpect(jsonPath("$[1].urlImagen", is("http://localhost:8080/files/Donald.jpg")));
		
		verify(personajeServicio).buscarPorArgs(any(),any(),any(),any());
		verify(converter, times(2)).convertirPersonajeAGetPersonajeDto(any());
	}
	
	@Test
	void listarPersonajesSinPersonajesTest() throws Exception {
		when(personajeServicio.buscarPorArgs(any(),any(),any(),any())).thenReturn(Page.empty());
		
		mockMvc.perform(get("/characters"))
				.andExpect(status().isNotFound());
		
		verify(personajeServicio).buscarPorArgs(any(),any(),any(),any());
		verify(converter, never()).convertirPersonajeAGetPersonajeDto(any());
	}
	
	@Test
	void listarPersonajesConNombrePresenteTest() throws Exception {
		Personaje personaje = new Personaje(1L, "http://localhost:8080/files/Mickey.jpg", "Mickey Mouse", 22, 30, null, null);
		
		when(personajeServicio.buscarPorNombreIgnoreCase("Mickey Mouse")).thenReturn(Optional.of(personaje));
		when(converter.convertirPersonajeAGetPersonajeDto(personaje))
						.thenReturn(mapper.map(personaje, GetPersonajeDto.class));
		
		mockMvc.perform(get("/characters?name=Mickey Mouse"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].nombre", is("Mickey Mouse")))
				.andExpect(jsonPath("$[0].urlImagen", is("http://localhost:8080/files/Mickey.jpg")))
				.andExpect(jsonPath("$[1]").doesNotExist());
		
		verify(personajeServicio, never()).buscarPorArgs(any(),any(),any(),any());
		verify(converter).convertirPersonajeAGetPersonajeDto(personaje);
		verify(personajeServicio).buscarPorNombreIgnoreCase("Mickey Mouse");
		}
	
	@Test
	void listarPersonajesConNombrePresenteNoEncontradoTest() throws Exception {
		Personaje personaje = new Personaje(1L, "http://localhost:8080/files/Mickey.jpg", "Mickey Mouse", 22, 30, null, null);
		
		when(personajeServicio.buscarPorNombreIgnoreCase("Pato Donald")).thenReturn(Optional.empty());
		
		mockMvc.perform(get("/characters?name=Pato Donald"))
				.andExpect(status().isNotFound());
		
		verify(personajeServicio, never()).buscarPorArgs(any(),any(),any(),any());
		verify(converter, never()).convertirPersonajeAGetPersonajeDto(personaje);
		verify(personajeServicio).buscarPorNombreIgnoreCase("Pato Donald");
	}
	
	@Test
	void buscarUnPersonajeTest() throws Exception {
		Personaje personaje = new Personaje(1L, "http://localhost:8080/files/Mickey.jpg", "Mickey Mouse", 22, 30, null, null);
		
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		
		mockMvc.perform(get("/characters/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("nombre", is("Mickey Mouse")))
				.andExpect(jsonPath("urlImagen", is("http://localhost:8080/files/Mickey.jpg")));
		
		verify(personajeServicio).buscarPorId(1L);
	}
	
	@Test
	void buscarUnPersonajeNoExistenteTest() throws Exception {
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.empty());
		
		mockMvc.perform(get("/characters/1"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("nombre").doesNotExist())
				.andExpect(jsonPath("urlImagen").doesNotExist());
		
		verify(personajeServicio).buscarPorId(1L);
	}
	
	@Test
	void crearUnPersonajeTest() throws Exception {
		Personaje personaje = new Personaje(1L, "http://localhost:8080/files/Mickey.jpg", "Mickey Mouse", 22, 30, null, null);
		String personajeJson = "{\"nombre\":\"Mickey Mouse\",\"edad\":22,\"peso\":30, \"historia\":null}";
		
		MockMultipartFile archivoJsonPersonaje = new MockMultipartFile("personaje", "personaje.json", "application/json", personajeJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(personajeServicio.existePorNombre(personaje.getNombre())).thenReturn(false);
		when(converter.convertirCrearYEditarPersonajeDtoAPersonaje(any())).thenReturn(personaje);
		when(personajeServicio.guardarImagenYAgregarUrlImagen(eq(personaje), any())).thenReturn(personaje);
		when(personajeServicio.guardar(personaje)).thenReturn(personaje);
		
		mockMvc.perform(multipart("/characters").file(archivoJsonPersonaje).file(archivoImagen))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("urlImagen", is("http://localhost:8080/files/Mickey.jpg")))
				.andExpect(jsonPath("nombre", is("Mickey Mouse")))
				.andExpect(jsonPath("edad", is(22)))
				.andExpect(jsonPath("peso", is(30.0)));
		
		verify(personajeServicio).existePorNombre(personaje.getNombre());
		verify(converter).convertirCrearYEditarPersonajeDtoAPersonaje(any());
		verify(personajeServicio).guardarImagenYAgregarUrlImagen(eq(personaje), any());
		verify(personajeServicio).guardar(personaje);
	}
	
	@Test
	void crearUnPersonajeConErroresValidacionTest() throws Exception {
		String personajeJson = "{\"nombre\":\"    \",\"edad\":22,\"peso\":30, \"historia\":null}";
		
		MockMultipartFile archivoJsonPersonaje = new MockMultipartFile("personaje", "personaje.json", "application/json", personajeJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		mockMvc.perform(multipart("/characters").file(archivoJsonPersonaje).file(archivoImagen))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("Existen errores de validacion")));
		
		verify(personajeServicio, never()).existePorNombre(any());
		verify(converter, never()).convertirCrearYEditarPersonajeDtoAPersonaje(any());
		verify(personajeServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(personajeServicio, never()).guardar(any());
	}
	
	@Test
	void crearUnPersonajeQueYaExisteTest() throws Exception {
		String personajeJson = "{\"nombre\":\"Mickey Mouse\",\"edad\":22,\"peso\":30, \"historia\":null}";
		
		MockMultipartFile archivoJsonPersonaje = new MockMultipartFile("personaje", "personaje.json", "application/json", personajeJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(personajeServicio.existePorNombre("Mickey Mouse")).thenReturn(true);
		
		mockMvc.perform(multipart("/characters").file(archivoJsonPersonaje).file(archivoImagen))
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("El personaje Mickey Mouse ya existe")));
		
		verify(personajeServicio).existePorNombre("Mickey Mouse");
		verify(converter, never()).convertirCrearYEditarPersonajeDtoAPersonaje(any());
		verify(personajeServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(personajeServicio, never()).guardar(any());
	}
	
	@Test
	void crearUnPersonajeSinPartesTest() throws Exception {
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		mockMvc.perform(multipart("/characters").file(archivoImagen))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		
		verify(personajeServicio, never()).existePorNombre(any());
		verify(converter, never()).convertirCrearYEditarPersonajeDtoAPersonaje(any());
		verify(personajeServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(personajeServicio, never()).guardar(any());
	}
	
	@Test
	void editarUnPersonajeTest() throws Exception {
		Personaje personaje = new Personaje(1L, "http://localhost:8080/files/Mickey.jpg", "Mickey Mouse", 22, 30, null, null);
		CrearYEditarPersonajeDto personajeDto = new CrearYEditarPersonajeDto("Mickey Mouse", 22, 30, null);
		String personajeJson = "{\"nombre\":\"Mickey Mouse\",\"edad\":22,\"peso\":30,\"historia\":null}";
		
		MockMultipartFile archivoJsonPersonaje = new MockMultipartFile("personaje", "personaje.json", "application/json", personajeJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		when(converter.convertirCrearYEditarPersonajeDtoAPersonaje(personajeDto, personaje)).thenReturn(personaje);
		when(personajeServicio.guardarImagenYAgregarUrlImagen(eq(personaje), any())).thenReturn(personaje);
		when(personajeServicio.editar(personaje)).thenReturn(personaje);
		
		mockMvc.perform(multipart("/characters/1").file(archivoJsonPersonaje).file(archivoImagen)
				.with( request -> {request.setMethod("PUT"); return request;}))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("urlImagen", is("http://localhost:8080/files/Mickey.jpg")))
					.andExpect(jsonPath("nombre", is("Mickey Mouse")))
					.andExpect(jsonPath("edad", is(22)))
					.andExpect(jsonPath("peso", is(30.0)));
		
		verify(personajeServicio).buscarPorId(1L);
		verify(converter).convertirCrearYEditarPersonajeDtoAPersonaje(personajeDto,personaje);
		verify(personajeServicio).guardarImagenYAgregarUrlImagen(eq(personaje), any());
		verify(personajeServicio).editar(personaje);
	}
	
	@Test
	void editarUnPersonajeConErroresValidacionTest() throws Exception {
		String personajeJson = "{\"nombre\":\"    \",\"edad\":22,\"peso\":30,\"historia\":null}";
		
		MockMultipartFile archivoJsonPersonaje = new MockMultipartFile("personaje", "personaje.json", "application/json", personajeJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		mockMvc.perform(multipart("/characters/1").file(archivoJsonPersonaje).file(archivoImagen)
				.with( request -> {request.setMethod("PUT"); return request;}))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("mensaje", is("Existen errores de validacion")));
		
		verify(personajeServicio, never()).buscarPorId(anyLong());
		verify(converter, never()).convertirCrearYEditarPersonajeDtoAPersonaje(any(),any());
		verify(personajeServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(personajeServicio, never()).editar(any());
	}
	
	@Test
	void editarUnPersonajeInexistenteTest() throws Exception {
		String personajeJson = "{\"nombre\":\"Mickey Mouse\",\"edad\":22,\"peso\":30,\"historia\":null}";
		
		MockMultipartFile archivoJsonPersonaje = new MockMultipartFile("personaje", "personaje.json", "application/json", personajeJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(PersonajeNulo.construir()));
		
		mockMvc.perform(multipart("/characters/1").file(archivoJsonPersonaje).file(archivoImagen)
				.with( request -> {request.setMethod("PUT"); return request;}))
					.andExpect(status().isNotFound());
		
		verify(personajeServicio).buscarPorId(1L);
		verify(converter, never()).convertirCrearYEditarPersonajeDtoAPersonaje(any(),any());
		verify(personajeServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(personajeServicio, never()).editar(any());
	}
	
	@Test
	void editarUnPersonajeSinPartesTest() throws Exception {
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		mockMvc.perform(multipart("/characters/1").file(archivoImagen)
				.with( request -> {request.setMethod("PUT"); return request;}))
					.andExpect(status().isBadRequest())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		
		verify(personajeServicio, never()).buscarPorId(anyLong());
		verify(converter, never()).convertirCrearYEditarPersonajeDtoAPersonaje(any(),any());
		verify(personajeServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(personajeServicio, never()).editar(any());
	}
	
	@Test
	void eliminarUnPersonajeSinAudiovisualesTestTest() throws Exception {
		Personaje personaje = new Personaje(1L, "http://localhost:8080/files/Mickey.jpg", "Mickey Mouse", 22, 30, null, Arrays.asList());
		
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		
		mockMvc.perform(delete("/characters/1"))
				.andExpect(status().isNoContent());
		
		verify(personajeServicio).buscarPorId(1L);
		verify(personajeServicio).borrar(personaje);
	}
	
	@Test
	void eliminarUnPersonajeConAudiovisualesTestTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", null);
		Audiovisual audiovisual = new Pelicula(1L, null, "Prueba", LocalDate.now(), 4, Arrays.asList(), genero);
		Personaje personaje = new Personaje(1L, "http://localhost:8080/files/Mickey.jpg", "Mickey Mouse", 22, 30, null, Arrays.asList(audiovisual));
		
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.of(personaje));
		
		mockMvc.perform(delete("/characters/1"))
				.andExpect(status().isNoContent());
		
		verify(personajeServicio).buscarPorId(1L);
		verify(personajeServicio).borrar(personaje);
	}
	
	@Test
	void eliminarUnPersonajeInexistenteTest() throws Exception {
		when(personajeServicio.buscarPorId(1L)).thenReturn(Optional.empty());
		
		mockMvc.perform(delete("/characters/1"))
				.andExpect(status().isNotFound());
		
		verify(personajeServicio).buscarPorId(1L);
		verify(personajeServicio, never()).borrar(any());
	}
}
