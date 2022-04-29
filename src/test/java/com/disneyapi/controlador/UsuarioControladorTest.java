package com.disneyapi.controlador;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;

import com.disneyapi.dto.usuario.GetUsuarioDto;
import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.modelo.Usuario;
import com.disneyapi.seguridad.SeguridadConfig;
import com.disneyapi.servicio.UsuarioServicio;
import com.disneyapi.util.converter.UsuarioDtoConverter;
import com.disneyapi.util.enumerados.RolUsuario;
import com.disneyapi.util.paginacion.PaginacionLinks;

@WebMvcTest(value = UsuarioControlador.class, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
		classes = {SeguridadConfig.class, AutorizacionFiltro.class})},
		excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class UsuarioControladorTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UsuarioServicio usuarioServicio;
	@MockBean
	private UsuarioDtoConverter converter;
	@MockBean
	private PaginacionLinks links;
	
	ModelMapper mapper;
	
	@BeforeEach
	void setup(){
		mapper = new ModelMapper();
	}
	
	@Test
	void listarUsuariosTest() throws Exception {
		Usuario usuario = new Usuario(1L, "Jose Fernandez", "jose1989", "123456", "jfernandez@gmail.com", Arrays.asList(RolUsuario.ROLE_USER));
		Usuario usuario2 = new Usuario(2L, "Claudia Almada", "claualmi", "123456", "calmada@gmail.com", Arrays.asList(RolUsuario.ROLE_USER));
		List<Usuario> lista = Arrays.asList(usuario, usuario2);
		Page<Usuario> usuarios = new PageImpl<>(lista);
		
		when(usuarioServicio.buscarTodos(any())).thenReturn(usuarios);
		when(converter.convertirUsuarioAGetUsuarioDto(usuario)).thenReturn(mapper.map(usuario, GetUsuarioDto.class));
		when(converter.convertirUsuarioAGetUsuarioDto(usuario2)).thenReturn(mapper.map(usuario2, GetUsuarioDto.class));
		
		mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("[0].nombreCompleto", is("Jose Fernandez")))
				.andExpect(jsonPath("[0].nombreUsuario", is("jose1989")))
				.andExpect(jsonPath("[0].email", is("jfernandez@gmail.com")))
				.andExpect(jsonPath("[1].nombreCompleto", is("Claudia Almada")))
				.andExpect(jsonPath("[1].nombreUsuario", is("claualmi")))
				.andExpect(jsonPath("[1].email", is("calmada@gmail.com")));
		
		verify(usuarioServicio).buscarTodos(any());
		verify(converter, times(2)).convertirUsuarioAGetUsuarioDto(any());
	}
	
	@Test
	void listarUsuariosSinUsuariosTest() throws Exception {	
		when(usuarioServicio.buscarTodos(any())).thenReturn(Page.empty());
		
		mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		verify(usuarioServicio).buscarTodos(any());
		verify(converter, never()).convertirUsuarioAGetUsuarioDto(any());
	}
	
	@Test
	void buscarUnUsuarioTest() throws Exception {
		Usuario usuario = new Usuario(1L, "Jose Fernandez", "jose1989", "123456", "jfernandez@gmail.com", Arrays.asList(RolUsuario.ROLE_USER));
		
		when(usuarioServicio.buscarPorId(1L)).thenReturn(Optional.of(usuario));
		when(converter.convertirUsuarioAGetUsuarioDto(usuario)).thenReturn(mapper.map(usuario, GetUsuarioDto.class));
		
		mockMvc.perform(get("/users/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("nombreCompleto", is("Jose Fernandez")))
				.andExpect(jsonPath("nombreUsuario", is("jose1989")))
				.andExpect(jsonPath("email", is("jfernandez@gmail.com")));
		
		verify(usuarioServicio).buscarPorId(1L);
		verify(converter).convertirUsuarioAGetUsuarioDto(usuario);
	}
	
	@Test
	void buscarUnUsuarioInexistenteTest() throws Exception {
		when(usuarioServicio.buscarPorId(1L)).thenReturn(Optional.empty());
		
		mockMvc.perform(get("/users/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		verify(usuarioServicio).buscarPorId(1L);
		verify(converter, never()).convertirUsuarioAGetUsuarioDto(any());
	}
	
	@Test
	void eliminarUnUsuarioTest() throws Exception {
		when(usuarioServicio.existePorId(1L)).thenReturn(true);
		
		mockMvc.perform(delete("/users/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
		
		verify(usuarioServicio).existePorId(1L);
		verify(usuarioServicio).borrarPorId(1L);
	}
	
	@Test
	void eliminarUnUsuarioInexistenteTest() throws Exception {
		when(usuarioServicio.existePorId(1L)).thenReturn(false);
		
		mockMvc.perform(delete("/users/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		
		verify(usuarioServicio).existePorId(1L);
		verify(usuarioServicio, never()).borrarPorId(1L);
	}
}
