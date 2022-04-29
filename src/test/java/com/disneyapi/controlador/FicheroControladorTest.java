package com.disneyapi.controlador;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.disneyapi.filtro.AutorizacionFiltro;
import com.disneyapi.seguridad.SeguridadConfig;
import com.disneyapi.servicio.AlmacenamientoServicio;
 
@WebMvcTest(value = FicheroControlador.class, excludeFilters = {
    	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
    	classes = { SeguridadConfig.class, AutorizacionFiltro.class})},  
		excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class FicheroControladorTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	private AlmacenamientoServicio almacenamientoServicio;
	
	@Test
	void serveFile() throws Exception {
		String nombreArchivo = "miImagen.png";
		Path archivo = Paths.get("imagenes-dir").resolve(nombreArchivo);
		Resource resource = new UrlResource(archivo.toUri());
		
		when(almacenamientoServicio.cargarComoResource(nombreArchivo)).thenReturn(resource);
		
		mockMvc.perform(get("/files/miImagen.png"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.IMAGE_PNG));
		
		verify(almacenamientoServicio).cargarComoResource(nombreArchivo);
	}
}
