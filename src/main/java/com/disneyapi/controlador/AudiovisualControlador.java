package com.disneyapi.controlador;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.disneyapi.dto.GetPersonajeDto;
import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.modelo.objetonulo.AudiovisualNulo;
import com.disneyapi.servicio.AudiovisualServicio;
import com.disneyapi.util.paginacion.PaginacionLinks;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class AudiovisualControlador {

	private final AudiovisualServicio audiovisualServicio;
	private final AudiovisualDtoConverter converter;
	private final PaginacionLinks paginacionLinks;
	
	@GetMapping()
	public ResponseEntity<List<GetPersonajeDto>> getPersonajes(
			@RequestParam("name") Optional<String> titulo,
			@RequestParam("genre") Optional<String> genero,
			@RequestParam("order") Optional<String> orden,
			@PageableDefault(size = 20, page = 0) Pageable pageable, HttpServletRequest request){
		
		if(titulo.isPresent()) {
			Audiovisual audiovisual = audiovisualServicio.buscarPorTitulo(titulo.get()).orElse(AudiovisualNulo.contruir());
			GetPersonajeDto getPersonajeDto = converter.convertirAudiovisualAGetAudiovisualDto(audiovisual);
			
			if(audiovisual.esNula())
				return ResponseEntity.notFound().build();
			else
				return ResponseEntity.ok(Arrays.asList(getPersonajeDto));
		}else {
			Page<Audiovisual> audiovisuales = audiovisualServicio.buscarPorArgs(genero, orden, pageable);
			
			if(audiovisuales.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			Page<GetAudiovisualDto> getAudiovisualesDto = audiovisuales.map(audiovisual -> converter.convertirAudiovisualAGetAudiovisualDto(audiovisual));
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
			
			return ResponseEntity.ok().header("link", paginacionLinks.crearLinkHeader(getAudiovisualesDto, builder))
					.body(getAudiovisualesDto.getContent());
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Audiovisual> obtenerDetallePersonaje(@PathVariable Long id){
		Audiovisual audiovisual = audiovisualServicio.buscarPorId(id).orElse(AudiovisualNulo.contruir());
		
		if(audiovisual.esNula())
			return ResponseEntity.notFound().build();
		else
			return ResponseEntity.ok(audiovisual);
	}
	
}
