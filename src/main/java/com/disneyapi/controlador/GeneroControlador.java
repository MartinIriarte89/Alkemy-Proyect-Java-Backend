package com.disneyapi.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.disneyapi.dto.CrearGeneroDto;
import com.disneyapi.modelo.Genero;
import com.disneyapi.servicio.GeneroServicio;
import com.disneyapi.util.converter.GeneroDtoConverter;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genre")
public class GeneroControlador {

	private final GeneroServicio generoServicio;
	private final GeneroDtoConverter converter;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Genero> nuevoGenero(@RequestPart("genero") CrearGeneroDto generoDto,
			@RequestPart("imagen") MultipartFile imagen) {
		Genero genero = generoServicio
				.guardarImagenYAgregarUrlImagen(converter.convertirCrearGeneroDtoAGenero(generoDto), imagen);

		return ResponseEntity.status(HttpStatus.CREATED).body(generoServicio.guardar(genero));
	}
}
