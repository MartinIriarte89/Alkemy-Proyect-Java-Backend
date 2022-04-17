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

import com.disneyapi.error.ApiError;
import com.disneyapi.servicio.AlmacenamientoServicio;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RequiredArgsConstructor
@Controller
public class FicheroControlador {

	private final AlmacenamientoServicio storageService;

	@ApiOperation(value = "Manejo de imagenes.", 
			notes = "Provee un mecanismo para manejar las imagenes de los distintos modelos y retornarlas.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Resource.class),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	@GetMapping(value = "/files/{nombreArchivo:.+}")
	
	@ResponseBody
	public ResponseEntity<Resource> serveFile(
			@ApiParam(value = "Nombre de la imagen que se desea buscar", required = true, type = "String")
			@PathVariable String nombreArchivo, 
			@ApiIgnore HttpServletRequest request) {
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
