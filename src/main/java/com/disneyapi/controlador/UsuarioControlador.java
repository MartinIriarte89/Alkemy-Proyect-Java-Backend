package com.disneyapi.controlador;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.disneyapi.dto.usuario.GetUsuarioDto;
import com.disneyapi.modelo.Usuario;
import com.disneyapi.modelo.objetonulo.UsuarioNulo;
import com.disneyapi.servicio.UsuarioServicio;
import com.disneyapi.util.converter.UsuarioDtoConverter;
import com.disneyapi.util.paginacion.PaginacionLinks;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsuarioControlador {
	
	private final UsuarioServicio usuarioServicio;
	private final UsuarioDtoConverter converter;
	private final PaginacionLinks paginacionLinks;
	
	@GetMapping
	public ResponseEntity<List<GetUsuarioDto>> listarUsuarios(
			@PageableDefault(page = 0, size = 50, sort = "nombreCompleto",direction = Direction.ASC)
			Pageable pageable, HttpServletRequest request){
		Page<Usuario> usuarios = usuarioServicio.buscarTodos(pageable);
		
		if(usuarios.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
		List<GetUsuarioDto> getUsuariosDtos = usuarios.getContent().stream()
														.map(converter::convertirUsuarioAGetUsuarioDto)
														.collect(Collectors.toList());						
														
		return ResponseEntity.status(HttpStatus.OK).header("link", paginacionLinks.crearLinkHeader(usuarios, builder))
				.body(getUsuariosDtos);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<GetUsuarioDto> obtenerUsuario(
			@PathVariable Long id){
		Usuario usuario = usuarioServicio.buscarPorId(id).orElse(UsuarioNulo.construir());
		
		if(usuario.esNulo()) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(converter.convertirUsuarioAGetUsuarioDto(usuario));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<GetUsuarioDto> borrarUsuario(
			@PathVariable Long id){
		if(!usuarioServicio.existePorId(id)) {
			return ResponseEntity.notFound().build();
		}
		usuarioServicio.borrarPorId(id);
		return ResponseEntity.noContent().build();
	}
}
