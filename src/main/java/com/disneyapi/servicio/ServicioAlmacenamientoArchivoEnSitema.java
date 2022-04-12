package com.disneyapi.servicio;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.martiniriarte.error.exceptions.AlmacenamientoArchivoNoEncontradoException;
import com.martiniriarte.error.exceptions.AlmacenamientoException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServicioAlmacenamientoArchivoEnSitema implements ServicioAlmacenamiento {

	private final Path ubicacionRaiz;

	public ServicioAlmacenamientoArchivoEnSitema(@Value("${carga.ubicacion-raiz}") String path) {
		this.ubicacionRaiz = Paths.get(path);
	}

	@Override
	public String guardar(MultipartFile file) {
		if (file.isEmpty()) {
			throw new AlmacenamientoException("No se pudo almacenar el archivo vacío ");
		}

		String nombreOriginal = file.getOriginalFilename();
		log.info(nombreOriginal);
		String filename = "";
		if (nombreOriginal != null) {
			filename = StringUtils.cleanPath(nombreOriginal);
			log.info(filename);
		}

		String extension = StringUtils.getFilenameExtension(filename);
		String justFilename = filename.replace("." + extension, "");
		String storedFilename = System.currentTimeMillis() + "_" + justFilename + "." + extension;
		log.info(storedFilename);
		
		if (filename.contains("..")) {
			// This is a security check
			throw new AlmacenamientoException(
					"No se puede almacenar el archivo con la ruta relativa fuera del directorio actual " + filename);
		}

		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, this.ubicacionRaiz.resolve(storedFilename), StandardCopyOption.REPLACE_EXISTING);
			
			return storedFilename;
		} catch (IOException e) {
			throw new AlmacenamientoException("No se pudo almacenar el archivo " + filename, e);
		}

	}

	@Override
	public Stream<Path> obtenerTodasLasRutas() {
		try {
			return Files.walk(this.ubicacionRaiz, 1).filter(path -> !path.equals(this.ubicacionRaiz))
					.map(this.ubicacionRaiz::relativize);
		} catch (IOException e) {
			throw new AlmacenamientoException("Error al leer los archivos almacenados", e);
		}

	}

	@Override
	public Path obtenerRuta(String filename) {
		return ubicacionRaiz.resolve(filename);
	}

	@Override
	public Resource cargarComoResource(String filename) {
		try {
			Path file = obtenerRuta(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new AlmacenamientoArchivoNoEncontradoException("No se pudo leer el archivo: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new AlmacenamientoArchivoNoEncontradoException("No se pudo leer el archivo: " + filename, e);
		}
	}

	@Override
	public void borrarTodos() {
		FileSystemUtils.deleteRecursively(ubicacionRaiz.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(ubicacionRaiz);
		} catch (IOException e) {
			throw new AlmacenamientoException("No se pudo inicializar el almacenamiento", e);
		}
	}

	@Override
	public void borrar(String filename) {
		String justFilename = StringUtils.getFilename(filename);
		try {
			Path file = obtenerRuta(justFilename);
			Files.deleteIfExists(file);
		} catch (IOException e) {
			throw new AlmacenamientoException("Error al eliminar un fichero", e);
		}

	}
}
