package com.disneyapi.servicio;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ServicioAlmacenamiento {

	void init();

	String guardar(MultipartFile file);

	Stream<Path> obtenerTodasLasRutas();

	Path obtenerRuta(String filename);

	Resource cargarComoResource(String filename);

	void borrar(String filename);

	void borrarTodos();
}
