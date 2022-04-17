package com.disneyapi.controlador;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.disneyapi.dto.CrearGeneroDto;
import com.disneyapi.error.ApiError;
import com.disneyapi.error.exception.ValidacionException;
import com.disneyapi.modelo.Genero;
import com.disneyapi.servicio.GeneroServicio;
import com.disneyapi.util.converter.GeneroDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genre")
public class GeneroControlador {

	private final GeneroServicio generoServicio;
	private final GeneroDtoConverter converter;
	private final PaginacionLinks paginacionLinks;

	@ApiOperation(value = "listar todos los géneros", 
			notes = "Provee un mecanismo para listar todos los géneros existentes."
					+ " Lo devuelve compaginado, y se puede cambiar el tamaño por defecto"
					+ " agregando una variable \"?size=\" y la dirección del orden con \"?direction\""
					+ " al path.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Genero.class, responseContainer = "List"),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@GetMapping
	public ResponseEntity<List<Genero>> listarGeneros(
			@ApiIgnore @PageableDefault(page = 0, size = 20, sort = "nombre") Pageable pageable, 
			@ApiIgnore HttpServletRequest request) {
		Page<Genero> generos = generoServicio.buscarTodos(pageable);

		if (generos.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

		return ResponseEntity.ok().header("link", paginacionLinks.crearLinkHeader(generos, builder))
				.body(generos.getContent());
	}

	@ApiOperation(value = "Crear un género", 
			notes = "Provee un mecanismo para crear un género nuevo. Se necesitan permisos de administrador")
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Created", response = Genero.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 409, message = "Conflict", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Genero> nuevoGenero(
			@ApiParam(value = "Representacion Json del género que se desea crear.", required = true, type = "object")
			@Valid @RequestPart("genero") CrearGeneroDto generoDto,
			@ApiIgnore final Errors errores,
			@ApiParam(value = "Archivo de imagen que se guarda en el género.", required = false, type = "File")
			@RequestPart("imagen") MultipartFile imagen) {
		if(errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		Genero genero = generoServicio
				.guardarImagenYAgregarUrlImagen(converter.convertirCrearGeneroDtoAGenero(generoDto), imagen);

		return ResponseEntity.status(HttpStatus.CREATED).body(generoServicio.guardar(genero));
	}
}
