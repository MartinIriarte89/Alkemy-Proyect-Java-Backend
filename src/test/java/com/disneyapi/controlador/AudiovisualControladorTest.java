package com.disneyapi.controlador;

import static org.mockito.Mockito.verify;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.Pelicula;
import com.disneyapi.servicio.AudiovisualServicio;

@WebMvcTest(controllers = AudiovisualControlador.class)
class AudiovisualControladorTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AudiovisualServicio audiovisualServicio;
	
	@Test
	void testListarAudiovisuales() throws Exception {
		Audiovisual audiovisual = new Pelicula(1L, null, "Pelicula Prueba", LocalDate.now(), 4, null, null);
		when(audiovisualServicio.buscarPorId(1L)).thenReturn(Optional.of(audiovisual));
		
		mockMvc.perform(get("/movies/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.titulo").value("Pelicula Prueba"))
				.andExpect(jsonPath("$.calificacion").value(4));
		
		verify(audiovisualServicio).buscarPorId(1L);
	}
}
