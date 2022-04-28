package com.disneyapi.controlador;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

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
import org.springframework.test.web.servlet.MockMvc;

import com.disneyapi.dto.personaje.GetPersonajeDto;
import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.modelo.Personaje;
import com.disneyapi.seguridad.SeguridadConfig;
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
}
