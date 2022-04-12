package com.disneyapi.servicio;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.disneyapi.modelo.Genero;
import com.disneyapi.repositorio.GeneroRepositorio;
import com.disneyapi.servicio.base.BaseServicio;

@Service
public class GeneroServicio extends BaseServicio<Genero, Long, GeneroRepositorio> {

	private AlmacenamientoServicio almacenamientoServicio;
	
	public GeneroServicio(GeneroRepositorio repositorio, AlmacenamientoServicio almacenamientoServicio) {
		super(repositorio);
		this.almacenamientoServicio = almacenamientoServicio;
	}

	public Genero guardarImagenYAgregarUrlImagen(Genero genero, MultipartFile archivo) {
		String urlImagen = null;

		if (!archivo.isEmpty()) {
			String imagen = almacenamientoServicio.guardar(archivo);
			urlImagen = MvcUriComponentsBuilder.fromMethodName(FicheroControlador.class, "serveFile", imagen, null)
					.build().toUriString();
		}
		genero.setUrlImagen(urlImagen);

		return genero;
	}
}
