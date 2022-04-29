package com.disneyapi.controlador;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
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

import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.modelo.Genero;
import com.disneyapi.seguridad.SeguridadConfig;
import com.disneyapi.servicio.GeneroServicio;
import com.disneyapi.util.converter.GeneroDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;


@WebMvcTest(value = GeneroControlador.class, excludeFilters = {
    	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
    	classes = { SeguridadConfig.class, AutorizacionFiltro.class})},  
		excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class GeneroControladorTest {

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	private GeneroServicio generoServicio;
	@MockBean
	private GeneroDtoConverter converter;
	@MockBean
	private PaginacionLinks links;
	
	@Test
	void listarGenerosTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", "http://localhost:8080/files/generoImagen.jpg");
		Genero genero2 = new Genero(1L, "Animado", "http://localhost:8080/files/generoImagen2.jpg");
		List<Genero> lista = Arrays.asList(genero, genero2);
		Page<Genero> generos = new PageImpl<>(lista);
		
		when(generoServicio.buscarTodos(any())).thenReturn(generos);
		
		mockMvc.perform(get("/genres"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].nombre", is("Musical")))
				.andExpect(jsonPath("$[1].nombre", is("Animado")))
				.andExpect(jsonPath("$[0].urlImagen", is("http://localhost:8080/files/generoImagen.jpg")))
				.andExpect(jsonPath("$[1].urlImagen", is("http://localhost:8080/files/generoImagen2.jpg")));
		
		verify(generoServicio).buscarTodos(any());
	}
	
	@Test
	void listarGenerosSinGenerosTest() throws Exception {
		when(generoServicio.buscarTodos(any())).thenReturn(Page.empty());
		
		mockMvc.perform(get("/genres"))
				.andExpect(status().isNotFound());
		
		verify(generoServicio).buscarTodos(any());
	}
	
	@Test
	void crearGeneroTest() throws Exception {
		Genero genero = new Genero(1L, "Musical", "http://localhost:8080/files/generoImagen.jpg");
		String generoJson = "{\"nombre\":\"Musical\"}";
		
		MockMultipartFile archivoJsonGenero = new MockMultipartFile("genero", "genero.json", "application/json", generoJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
	
		when(converter.convertirCrearGeneroDtoAGenero(any())).thenReturn(genero);
		when(generoServicio.guardarImagenYAgregarUrlImagen(eq(genero), any())).thenReturn(genero);
		when(generoServicio.guardar(genero)).thenReturn(genero);
		
		mockMvc.perform(multipart("/genres").file(archivoJsonGenero).file(archivoImagen))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("urlImagen", is("http://localhost:8080/files/generoImagen.jpg")))
				.andExpect(jsonPath("nombre", is("Musical")));
		
		verify(converter).convertirCrearGeneroDtoAGenero(any());
		verify(generoServicio).guardarImagenYAgregarUrlImagen(eq(genero), any());
		verify(generoServicio).guardar(genero);
	}
	
	@Test
	void crearGeneroConErroresValidacionTest() throws Exception {
		String generoJson = "{\"nombre\":\"    \"}";
		
		MockMultipartFile archivoJsonGenero = new MockMultipartFile("genero", "genero.json", "application/json", generoJson.getBytes());
		MockMultipartFile archivoImagen = new MockMultipartFile("imagen", "miImagen.jpg", "image/jpeg", "null".getBytes());
		
		mockMvc.perform(multipart("/genres").file(archivoJsonGenero).file(archivoImagen))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("mensaje", is("Existen errores de validacion")));
		
		verify(converter, never()).convertirCrearGeneroDtoAGenero(any());
		verify(generoServicio, never()).guardarImagenYAgregarUrlImagen(any(), any());
		verify(generoServicio, never()).guardar(any());
	}
}
