package com.disneyapi.controlador;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.disneyapi.servicio.AlmacenamientoServicio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FicheroControlador {

	private final AlmacenamientoServicio almacenamientoServicio;

	@GetMapping(value = "/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String nombreArchivo, HttpServletRequest request) {
		Resource archivo = almacenamientoServicio.cargarComoResource(nombreArchivo);

		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(archivo.getFile().getAbsolutePath());
		} catch (IOException ex) {
			log.info("No se pudo determinar el tipo de archivo.");
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(archivo);
	}
}
