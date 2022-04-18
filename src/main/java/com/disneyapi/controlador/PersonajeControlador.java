package com.disneyapi.controlador;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.disneyapi.dto.CrearYEditarPersonajeDto;
import com.disneyapi.dto.GetPersonajeDto;
import com.disneyapi.error.ApiError;
import com.disneyapi.error.exception.PersonajeYaExisteException;
import com.disneyapi.error.exception.ValidacionException;
import com.disneyapi.modelo.Personaje;
import com.disneyapi.modelo.objetonulo.PersonajeNulo;
import com.disneyapi.servicio.PersonajeServicio;
import com.disneyapi.util.converter.PersonajeDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/characters")
public class PersonajeControlador {

	private final PersonajeServicio personajeServicio;
	private final PersonajeDtoConverter converter;
	private final PaginacionLinks paginacionLinks;
	
	@ApiOperation(value = "Obtener una lista de personajes", 
			notes = "Provee un mecanismo para obtener todos los personajes con paginación, "
					+ " tambien permite buscar por nombre, filtrar por edad, peso, título de"
					+ " películas o serie a las que pertenecen, ordenar con la variable"
					+ " \"?sort=\" por cualquier atributo, de manera asc o desc con la variable"
					+ " \"?direction=\" y se puede cambiar el tamaño por defecto de la paginación"
					+ " agregando una variable \"?size=\".")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = GetPersonajeDto.class, responseContainer = "List"),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@GetMapping()
	public ResponseEntity<List<GetPersonajeDto>> obtenerListaPersonajes(
			@ApiParam(value = "Cadena para buscar por nombre.", required = false, type = "String")
			@RequestParam("name") Optional<String> nombre,
			@ApiParam(value = "Valor numérico para filtrar por edad, igual o menor que.", required = false, type = "int")
			@RequestParam("age") Optional<Integer> edad,
			@ApiParam(value = "Valor numérico para filtrar por peso, igual o menor que.", required = false, type = "int")
			@RequestParam("weight") Optional<Double> peso,
			@ApiParam(value = "Cadena para buscar personajes pertenecientes a la película o serie dada.", required = false, type = "String")
			@RequestParam("movies") Optional<String>peliculaTitulo,
			@ApiIgnore @PageableDefault(size = 20, page = 0) Pageable pageable, 
			@ApiIgnore HttpServletRequest request){
		
		if(nombre.isPresent()) {
			Personaje personaje = personajeServicio.buscarPorNombreIgnoreCase(nombre.get()).orElse(PersonajeNulo.construir());
			GetPersonajeDto getPersonajeDto = converter.convertirPersonajeAGetPersonajeDto(personaje);
			
			if(personaje.esNulo())
				return ResponseEntity.notFound().build();
			else
				return ResponseEntity.ok(Arrays.asList(getPersonajeDto));
		}else {
			Page<Personaje> personajes = personajeServicio.buscarPorArgs(edad, peso, peliculaTitulo, pageable);
			
			if(personajes.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			Page<GetPersonajeDto> getPersonajesDto = personajes.map(converter::convertirPersonajeAGetPersonajeDto);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
			
			return ResponseEntity.ok().header("link", paginacionLinks.crearLinkHeader(getPersonajesDto, builder))
					.body(getPersonajesDto.getContent());
		}
	}
	
	@ApiOperation(value = "Obtener un personajes", 
			notes = "Provee un mecanismo para obtener un personaje con el id asignado.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Personaje.class, responseContainer = "List"),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@GetMapping("/{id}")
	public ResponseEntity<Personaje> obtenerDetallePersonaje(
			@ApiParam(value = "Id del personaje que se desea buscar", required = true, type = "int")
			@PathVariable Long id){
		Personaje personaje = personajeServicio.buscarPorId(id).orElse(PersonajeNulo.construir());
		
		if(personaje.esNulo())
			return ResponseEntity.notFound().build();
		else
			return ResponseEntity.ok(personaje);
	}
	@ApiOperation(value = "Crear un nuevo personaje", 
			notes = "Provee un mecanismo para crear un personaje nuevo, se requiere permisos"
					+ " de administrador. Espera recibir un archivo que sea el personaje"
					+ " con la key \"personaje\" y"
					+ " otro archivo con la imagen del mismo con la key \"imagen\".")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = Object.class),
			@ApiResponse(code = 201, message = "Created", response = Personaje.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 409, message = "Conflict", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Personaje> nuevoPersonaje(
			@ApiParam(value = "Representación Json del personaje a guardar. Se espera un CrearYEditarPersonajeDto, chequear el modelo.", required = true, type = "Json")
			@Valid @RequestPart("personaje") CrearYEditarPersonajeDto personajeDto,
			@ApiIgnore final Errors errores,
			@ApiParam(value = "Archivo de imagen perteneciente al personaje", required = false, type = "File")
			@RequestPart("imagen") MultipartFile imagen){
		if(errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		if(personajeServicio.existePorNombre(personajeDto.getNombre())) {
			throw new PersonajeYaExisteException(personajeDto.getNombre());
		}
		Personaje personaje = personajeServicio
					.guardarImagenYAgregarUrlImagen(
							converter.convertirCrearYEditarPersonajeDtoAPersonaje(personajeDto), imagen);	
	
		return ResponseEntity.status(HttpStatus.CREATED).body(personajeServicio.guardar(personaje));
	}
	
	@ApiOperation(value = "Edita un personaje", 
			notes = "Provee un mecanismo para editar un personaje existente, se requiere permisos"
					+ " de administrador. Espera recibir un archivo que sea la película o serie "
					+ " con la key \"personaje\" y"
					+ " otro archivo con la imagen del mismo con la key\"imagen\".")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Object.class),
			@ApiResponse(code = 201, message = "Created", response = Personaje.class),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 409, message = "Conflict", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Personaje> actualizarPersonaje(
			@ApiParam(value = "Id del personaje que se desea editar", required = false, type = "int")
			@PathVariable Long id,
			@ApiParam(value = "Representación Json del personaje a guardar. Se espera un CrearYEditarPersonajeDto, chequear el modelo.", required = true, type = "object")
			@Valid @RequestPart("personaje") CrearYEditarPersonajeDto personajeDto,
			@ApiIgnore final Errors errores,
			@ApiParam(value = "Archivo de imagen perteneciente al personaje", required = false, type = "file")
			@RequestPart("imagen") MultipartFile imagen){
		if(errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		Personaje personaje = personajeServicio.buscarPorId(id).orElse(PersonajeNulo.construir());
		
		if(personaje.esNulo()) {
			return ResponseEntity.notFound().build();
		}
		personaje = converter.convertirCrearYEditarPersonajeDtoAPersonaje(personajeDto, personaje);
		personaje = personajeServicio.guardarImagenYAgregarUrlImagen(personaje, imagen);
			
		return ResponseEntity.ok(personajeServicio.editar(personaje));
	}
	
	@ApiOperation(value = "Borra un personaje", 
			notes = "Provee un mecanismo para eliminar un personaje existente, se requiere permisos"
					+ " de administrador.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = Object.class),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Personaje> borrarPersonaje(
			@ApiParam(value = "Id del personaje que se desea eliminar", required = false, type = "int")
			@PathVariable Long id){
		Personaje personaje = personajeServicio.buscarPorId(id).orElse(PersonajeNulo.construir());
		
		if(personaje.esNulo()) {
			return ResponseEntity.notFound().build();
		}
		personajeServicio.borrar(personaje);
		return ResponseEntity.noContent().build();
	}
}
