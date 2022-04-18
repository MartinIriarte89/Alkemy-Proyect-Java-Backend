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

import com.disneyapi.dto.CrearAudiovisualDto;
import com.disneyapi.dto.EditarAudiovisualDto;
import com.disneyapi.dto.GetAudiovisualDto;
import com.disneyapi.error.ApiError;
import com.disneyapi.error.exception.AudiovisualYaExisteException;
import com.disneyapi.error.exception.ErrorTipoAudiovisual;
import com.disneyapi.error.exception.PersonajeNoEstaEnAudiovisualException;
import com.disneyapi.error.exception.PersonajeYaSeEncuentraEnException;
import com.disneyapi.error.exception.ValidacionException;
import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.Personaje;
import com.disneyapi.modelo.objetonulo.AudiovisualNulo;
import com.disneyapi.modelo.objetonulo.PersonajeNulo;
import com.disneyapi.servicio.AudiovisualServicio;
import com.disneyapi.servicio.PersonajeServicio;
import com.disneyapi.util.converter.AudiovisualDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class AudiovisualControlador {

	private final AudiovisualServicio audiovisualServicio;
	private final PersonajeServicio personajeServicio;
	private final AudiovisualDtoConverter converter;
	private final PaginacionLinks paginacionLinks;
	
	@ApiOperation(value = "Obtener una lista de películas o series", 
			notes = "Provee un mecanismo para obtener todos las películas y series con paginación, "
					+ " tambien permite buscar por nombre, filtrar por genero "
					+ "y ordenar por fecha de creaccion de manera asc o desc."
					+ " Se puede cambiar el tamaño por defecto de la paginación"
					+ " agregando una variable \"?size=\"")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = GetAudiovisualDto.class, responseContainer = "List"),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@GetMapping()
	public ResponseEntity<List<GetAudiovisualDto>> obtenerListaAudiovisuales(
			@ApiParam(value = "Cadena para buscar por titulo.", required = false, type = "String") 
			@RequestParam("name") Optional<String> titulo,
			@ApiParam(value = "Cadena para filtrar por el género", required = false, type = "String")
			@RequestParam("genre") Optional<String> genero,
			@ApiParam(value = "orden deseado, puede ser ASC o DESC.", required = false, type = "String")
			@RequestParam("order") Optional<String> orden,
			@ApiIgnore @PageableDefault(size = 20, page = 0) Pageable pageable, @ApiIgnore HttpServletRequest request) {

		if (titulo.isPresent()) {
			Audiovisual audiovisual = audiovisualServicio.buscarPorTituloIgnoreCase(titulo.get())
					.orElse(AudiovisualNulo.contruir());
			GetAudiovisualDto getAudiovisualDto = converter.convertirAudiovisualAGetAudiovisualDto(audiovisual);

			if (audiovisual.esNula())
				return ResponseEntity.notFound().build();
			else
				return ResponseEntity.ok(Arrays.asList(getAudiovisualDto));
		} else {
			Page<Audiovisual> audiovisuales = audiovisualServicio.buscarPorArgs(genero, orden, pageable);

			if (audiovisuales.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			Page<GetAudiovisualDto> getAudiovisualesDto = audiovisuales
					.map(converter::convertirAudiovisualAGetAudiovisualDto);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

			return ResponseEntity.ok().header("link", paginacionLinks.crearLinkHeader(getAudiovisualesDto, builder))
					.body(getAudiovisualesDto.getContent());
		}
	}
	
	@ApiOperation(value = "Obtener detalle de una película o serie", 
			notes = "Provee un mecanismo para obtener una pelicula o una serie con todos sus atributos")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Audiovisual.class),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@GetMapping("/{id}")
	public ResponseEntity<Audiovisual> obtenerDetalleAudiovisual(
			@ApiParam(value = "Id de la película o serie requerida.", required = true, type = "long")
			@PathVariable Long id) {
		Audiovisual audiovisual = audiovisualServicio.buscarPorId(id).orElse(AudiovisualNulo.contruir());

		if (audiovisual.esNula())
			return ResponseEntity.notFound().build();
		else
			return ResponseEntity.ok(audiovisual);
	}

	@ApiOperation(value = "Crear una nueva película o serie", 
			notes = "Provee un mecanismo para crear una pelicula o una serie nueva, se requiere permisos"
					+ " de administrador. Espera recibir un archivo que sea la película o serie"
					+ " con la key \"audiovisual\" y"
					+ " otro archivo con la imagen del mismo con la key \"imagen\".")
	
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Object.class),
			@ApiResponse(code = 201, message = "Created", response = Audiovisual.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 409, message = "Conflict", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<Audiovisual> nuevaAudiovisual(
			@ApiParam(value = "Representacion Json de la película o serie a crear. Se espera un CrearAudiovisualDto, chequear el modelo.", required = true, type = "object") 
			@Valid @RequestPart("audiovisual") CrearAudiovisualDto audiovisualDto,
			@ApiIgnore Errors errores,
			@ApiParam(value = "Archivo de imagen para cargar en la película o serie.", required = false, type = "File")
			@RequestPart("imagen") MultipartFile imagen) {
		if (errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		if(audiovisualServicio.existePorTitulo(audiovisualDto.getTitulo())) {
			throw new AudiovisualYaExisteException(audiovisualDto.getTitulo());
		}
		if(audiovisualDto.getTipo().equalsIgnoreCase("pelicula") || audiovisualDto.getTipo().equalsIgnoreCase("serie")) {
			throw new ErrorTipoAudiovisual(audiovisualDto.getTipo());
		}
		
		Audiovisual audiovisual = audiovisualServicio.guardarImagenYAgregarUrlImagen(
				converter.convertirCrearAudiovisualDtoAAudiovisual(audiovisualDto), imagen);

		return ResponseEntity.status(HttpStatus.CREATED).body(audiovisualServicio.guardar(audiovisual));
	}

	@ApiOperation(value = "Edita una película o serie", 
			notes = "Provee un mecanismo para editar una pelicula o una serie existente, se requiere permisos"
					+ " de administrador. Espera recibir un archivo que sea la película o serie "
					+ " con la key \"audiovisual\" y"
					+ " otro archivo con la imagen del mismo con la key\"imagen\".")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Object.class),
			@ApiResponse(code = 201, message = "Created", response = Audiovisual.class),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
			@ApiResponse(code = 409, message = "Conflict", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Audiovisual> editarAudiovisual(
			@ApiParam(value = "El id de la película o serie que será editada. Se espera un EditarAudiovisualDto, chequear el modelo.", required = true, type = "long") 
			@PathVariable Long id,
			@ApiParam(value = "Representacion Json de la película o serie a crear.", required = true, type = "object")
			@Valid @RequestPart("audiovisual") EditarAudiovisualDto audiovisualDto,
			@ApiIgnore final Errors errores,
			@ApiParam(value = "Archivo de imagen para cargar en la película o serie.", required = false, type = "File")
			@RequestPart("imagen") MultipartFile imagen){
		if(errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		Audiovisual audiovisual = audiovisualServicio.buscarPorId(id).orElse(AudiovisualNulo.contruir());
		
		if(audiovisual.esNula()) {
			return ResponseEntity.notFound().build();
		}
		audiovisual = converter.convertirEditarAudiovisualDtoAAudiovisual(audiovisualDto, audiovisual);
		audiovisual = audiovisualServicio.guardarImagenYAgregarUrlImagen(audiovisual, imagen);
		
		return ResponseEntity.ok(audiovisualServicio.editar(audiovisual));
	}
	
	@ApiOperation(value = "Borra una película o serie", 
			notes = "Provee un mecanismo para borrar una pelicula o una serie existente, se requiere permisos"
					+ " de administrador.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Object.class),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Audiovisual> borrarAudiovisual(
			@ApiParam(value = "El id de la película o serie que será eliminada.", required = true, type = "long") 
			@PathVariable Long id){
		Audiovisual audiovisual = audiovisualServicio.buscarPorId(id).orElse(AudiovisualNulo.contruir());
		
		if(audiovisual.esNula()) {
			return ResponseEntity.notFound().build();
		}
		audiovisualServicio.borrar(audiovisual);
		return ResponseEntity.noContent().build();
	}
	
	@ApiOperation(value = "Agrega un personaje a una película o serie", 
			notes = "Provee un mecanismo para agregara a un personaje a una pelicula o una serie existente, se requiere permisos"
					+ " de administrador.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Object.class),
			@ApiResponse(code = 201, message = "OK", response = Audiovisual.class),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@PostMapping("/{idAudiovisual}/characters/{idPersonaje}")
	public ResponseEntity<Audiovisual> agregarPersonajeAAudiovisual(
			@ApiParam(value = "El id de la película o serie a la que se le agregara el personaje.", required = true, type = "long") 
			@PathVariable Long idAudiovisual,
			@ApiParam(value = "El id del personaje que será agregado a la serie o película.", required = true, type = "long") 
			@PathVariable Long idPersonaje){
		Audiovisual audiovisual = audiovisualServicio.buscarPorId(idAudiovisual).orElse(AudiovisualNulo.contruir());
		Personaje personaje = personajeServicio.buscarPorId(idPersonaje).orElse(PersonajeNulo.construir()); 
		
		if(personaje.esNulo() || audiovisual.esNula()) {
			return ResponseEntity.notFound().build();
		}
		
		if(audiovisual.contieneA(personaje)) {
			throw new PersonajeYaSeEncuentraEnException(personaje.getNombre());
		}
		audiovisual.agregarA(personaje);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(audiovisualServicio.guardar(audiovisual));
	}
	
	@ApiOperation(value = "Quita a un personaje de una película o serie", 
			notes = "Provee un mecanismo para eliminar a un personaje de una pelicula o una serie existente"
					+ ", se requiere permisos de administrador.")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK", response = Audiovisual.class),
			@ApiResponse(code = 404, message = "Not Found", response = ApiError.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = ApiError.class)})
	
	@DeleteMapping("/{idAudiovisual}/characters/{idPersonaje}")
	public ResponseEntity<Audiovisual> eliminarPersonajeDeAudiovisual(
			@ApiParam(value = "El id de la película o serie a la que se le eliminara el personaje.", required = true, type = "long")
			@PathVariable Long idAudiovisual,
			@ApiParam(value = "El id del personaje que será eliminado de la serie o película.", required = true, type = "long")
			@PathVariable Long idPersonaje){
		Audiovisual audiovisual = audiovisualServicio.buscarPorId(idAudiovisual).orElse(AudiovisualNulo.contruir());
		Personaje personaje = personajeServicio.buscarPorId(idPersonaje).orElse(PersonajeNulo.construir()); 
		
		if(personaje.esNulo() || audiovisual.esNula()) {
			return ResponseEntity.notFound().build();
		}
		if(!audiovisual.contieneA(personaje)) {
			throw new PersonajeNoEstaEnAudiovisualException(personaje.getNombre());
		}
		audiovisual.eliminarA(personaje);
		
		return ResponseEntity.ok(audiovisualServicio.guardar(audiovisual));
	}
}
