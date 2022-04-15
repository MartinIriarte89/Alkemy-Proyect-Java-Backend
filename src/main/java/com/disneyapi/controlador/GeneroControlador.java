package com.disneyapi.controlador;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.disneyapi.dto.CrearGeneroDto;
import com.disneyapi.modelo.Genero;
import com.disneyapi.servicio.GeneroServicio;
import com.disneyapi.util.converter.GeneroDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genre")
public class GeneroControlador {

	private final GeneroServicio generoServicio;
	private final GeneroDtoConverter converter;
	private final PaginacionLinks paginacionLinks;

	@GetMapping
	public ResponseEntity<List<Genero>> listarGeneros(
			@PageableDefault(page = 0, size = 20, sort = "nombre") Pageable pageable, HttpServletRequest request) {
		Page<Genero> generos = generoServicio.buscarTodos(pageable);

		if (generos.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

		return ResponseEntity.ok().header("link", paginacionLinks.crearLinkHeader(generos, builder))
				.body(generos.getContent());
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Genero> nuevoGenero(@RequestPart("genero") CrearGeneroDto generoDto,
			@RequestPart("imagen") MultipartFile imagen) {
		Genero genero = generoServicio
				.guardarImagenYAgregarUrlImagen(converter.convertirCrearGeneroDtoAGenero(generoDto), imagen);

		return ResponseEntity.status(HttpStatus.CREATED).body(generoServicio.guardar(genero));
	}
}
