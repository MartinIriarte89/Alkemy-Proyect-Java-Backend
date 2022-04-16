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
@RequiredArgsConstructor
@Controller
public class FicheroControlador {

	private final AlmacenamientoServicio storageService;

	@GetMapping(value = "/files/{nombreArchivo:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String nombreArchivo, HttpServletRequest request) {
		Resource archivo = storageService.cargarComoResource(nombreArchivo);

		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(archivo.getFile().getAbsolutePath());
		} catch (IOException ex) {
			log.info("Could not determine file type.");
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(archivo);
	}
}
