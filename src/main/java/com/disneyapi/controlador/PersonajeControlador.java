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
import com.disneyapi.error.exception.PersonajeYaExisteException;
import com.disneyapi.error.exception.ValidacionException;
import com.disneyapi.modelo.Personaje;
import com.disneyapi.modelo.objetonulo.PersonajeNulo;
import com.disneyapi.servicio.PersonajeServicio;
import com.disneyapi.util.converter.PersonajeDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/characters")
public class PersonajeControlador {

	private final PersonajeServicio personajeServicio;
	private final PersonajeDtoConverter converter;
	private final PaginacionLinks paginacionLinks;
	
	@GetMapping()
	public ResponseEntity<List<GetPersonajeDto>> getPersonajes(
			@RequestParam("name") Optional<String> nombre,
			@RequestParam("age") Optional<Integer> edad,
			@RequestParam("weight") Optional<Double> peso,
			@RequestParam("movies") Optional<String>peliculaTitulo,
			@PageableDefault(size = 20, page = 0) Pageable pageable, HttpServletRequest request){
		
		if(nombre.isPresent()) {
			Personaje personaje = personajeServicio.buscarPorNombre(nombre).orElse(PersonajeNulo.construir());
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
	
	@GetMapping("/{id}")
	public ResponseEntity<Personaje> obtenerDetallePersonaje(@PathVariable Long id){
		Personaje personaje = personajeServicio.buscarPorId(id).orElse(PersonajeNulo.construir());
		
		if(personaje.esNulo())
			return ResponseEntity.notFound().build();
		else
			return ResponseEntity.ok(personaje);
	}
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Personaje> nuevoPersonaje(
			@Valid @RequestPart("personaje") CrearYEditarPersonajeDto personajeDto,
			final Errors errores,
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
	
	@PutMapping(name = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Personaje> actualizarPersonaje(
			@PathVariable Long id,
			@Valid @RequestPart("personaje") CrearYEditarPersonajeDto personajeDto,
			final Errors errores,
			@RequestPart("imagen") MultipartFile imagen){
		Personaje personaje = personajeServicio.editar(
				id, converter.convertirCrearYEditarPersonajeDtoAPersonaje(personajeDto), imagen);
		if(errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		if(personaje.esNulo())
			return ResponseEntity.notFound().build();
		else
			return ResponseEntity.ok(personaje);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Personaje> borrarPersonaje(@PathVariable Long id){
		Personaje personaje = personajeServicio.buscarPorId(id).orElse(PersonajeNulo.construir());
		
		if(personaje.esNulo()) {
			return ResponseEntity.notFound().build();
		}
		personajeServicio.borrar(personaje);
		return ResponseEntity.noContent().build();
	}
}
