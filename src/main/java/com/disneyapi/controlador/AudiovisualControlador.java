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

import com.disneyapi.dto.CrearYEditarAudiovisualDto;
import com.disneyapi.dto.GetAudiovisualDto;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class AudiovisualControlador {

	private final AudiovisualServicio audiovisualServicio;
	private final PersonajeServicio personajeServicio;
	private final AudiovisualDtoConverter converter;
	private final PaginacionLinks paginacionLinks;

	@GetMapping()
	public ResponseEntity<List<GetAudiovisualDto>> getAudiovisuales(
			@RequestParam("name") Optional<String> titulo,
			@RequestParam("genre") Optional<String> genero, 
			@RequestParam("order") Optional<String> orden,
			@PageableDefault(size = 20, page = 0) Pageable pageable, HttpServletRequest request) {

		if (titulo.isPresent()) {
			Audiovisual audiovisual = audiovisualServicio.buscarPorTitulo(titulo.get())
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

	@GetMapping("/{id}")
	public ResponseEntity<Audiovisual> obtenerDetalleAudiovisual(@PathVariable Long id) {
		Audiovisual audiovisual = audiovisualServicio.buscarPorId(id).orElse(AudiovisualNulo.contruir());

		if (audiovisual.esNula())
			return ResponseEntity.notFound().build();
		else
			return ResponseEntity.ok(audiovisual);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Audiovisual> nuevaAudiovisual(
			@Valid @RequestPart("audiovisual") CrearYEditarAudiovisualDto audiovisualDto,
			final Errors errores,
			@RequestPart("imagen") MultipartFile imagen) {
		if (errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		Audiovisual audiovisual = audiovisualServicio.guardarImagenYAgregarUrlImagen(
				converter.convertirCrearYEditarAudiovisualDtoAAudiovisual(audiovisualDto), imagen);

		return ResponseEntity.status(HttpStatus.CREATED).body(audiovisualServicio.guardar(audiovisual));
	}

	@PutMapping(name = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Audiovisual> editarAudiovisual(
			@PathVariable Long id,
			@Valid @RequestPart("audiovisual") CrearYEditarAudiovisualDto audiovisualDto,
			final Errors errores,
			@RequestPart("imagen") MultipartFile imagen){
		if(errores.hasErrors()) {
			throw new ValidacionException(errores.getAllErrors());
		}
		Audiovisual audiovisual = audiovisualServicio.editar(
				id, converter.convertirCrearYEditarAudiovisualDtoAAudiovisual(audiovisualDto), imagen);
		
		if(audiovisual.esNula())
			return ResponseEntity.notFound().build();
		else
			return ResponseEntity.ok(audiovisual);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Audiovisual> borrarAudiovisual(@PathVariable Long id){
		Audiovisual audiovisual = audiovisualServicio.buscarPorId(id).orElse(AudiovisualNulo.contruir());
		
		if(audiovisual.esNula()) {
			return ResponseEntity.notFound().build();
		}
		audiovisualServicio.borrar(audiovisual);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/{idAudiovisual}/characters/{idPersonaje}")
	public ResponseEntity<Audiovisual> agregarPersonajeAAudiovisual(
			@PathVariable Long idAudiovisual,
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
	
	@DeleteMapping("/{idAudiovisual}/characters/{idPersonaje}")
	public ResponseEntity<Audiovisual> eliminarPersonajeDeAudiovisual(
			@PathVariable Long idAudiovisual,
			@PathVariable Long idPersonaje){
		Audiovisual audiovisual = audiovisualServicio.buscarPorId(idAudiovisual).orElse(AudiovisualNulo.contruir());
		Personaje personaje = personajeServicio.buscarPorId(idPersonaje).orElse(PersonajeNulo.construir()); 
		
		if(personaje.esNulo() || audiovisual.esNula()) {
			return ResponseEntity.notFound().build();
		}
		else if(!audiovisual.contieneA(personaje)) {
			throw new PersonajeNoEstaEnAudiovisualException(personaje.getNombre());
		}
		audiovisual.eliminarA(personaje);
		
		return ResponseEntity.ok(audiovisualServicio.guardar(audiovisual));
	}
}
