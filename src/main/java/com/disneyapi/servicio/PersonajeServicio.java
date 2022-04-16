package com.disneyapi.servicio;

import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.disneyapi.controlador.FicheroControlador;
import com.disneyapi.modelo.Personaje;
import com.disneyapi.repositorio.PersonajeRepositorio;
import com.disneyapi.servicio.base.BaseServicio;

@Service
public class PersonajeServicio extends BaseServicio<Personaje, Long, PersonajeRepositorio> {

	private AlmacenamientoServicio almacenamientoServicio;

	@Autowired
	public PersonajeServicio(PersonajeRepositorio repositorio, AlmacenamientoServicio almacenamientoServicio) {
		super(repositorio);
		this.almacenamientoServicio = almacenamientoServicio;
	}

	public Optional<Personaje> buscarPorNombre(Optional<String> nombre) {
		return this.repositorio.findByNombre(nombre.get());
	}

	public Page<Personaje> buscarPorArgs(Optional<Integer> edad, Optional<Double> peso, Optional<String> peliculaTitulo,
			Pageable pageable) {

		Specification<Personaje> specEdadPersonajes = new Specification<Personaje>() {

			private static final long serialVersionUID = -1149887718564873709L;

			@Override
			public Predicate toPredicate(Root<Personaje> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				if (edad.isPresent()) {
					return criteriaBuilder.equal(root.get("edad"), edad.get());
				} else {
					return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
				}
			}
		};

		Specification<Personaje> specPesoPersonajes = new Specification<Personaje>() {

			private static final long serialVersionUID = 7343960081393395595L;

			@Override
			public Predicate toPredicate(Root<Personaje> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				if (peso.isPresent()) {
					return criteriaBuilder.lessThanOrEqualTo(root.get("peso"), peso.get());
				} else {
					return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
				}
			}
		};

		Specification<Personaje> specAudiovisualDePersonajes = new Specification<Personaje>() {

			private static final long serialVersionUID = -472330042883928358L;

			@Override
			public Predicate toPredicate(Root<Personaje> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				if (peliculaTitulo.isPresent()) {
					return criteriaBuilder.equal(root.join("audiovisuales").get("titulo"), peliculaTitulo.get());
				} else {
					return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
				}
			}
		};
		Specification<Personaje> todas = specEdadPersonajes.and(specPesoPersonajes).and(specAudiovisualDePersonajes);

		return this.repositorio.findAll(todas, pageable);
	}

	public Personaje guardarImagenYAgregarUrlImagen(Personaje personaje, MultipartFile archivo) {

		if (!archivo.isEmpty()) {
			String imagen = almacenamientoServicio.guardar(archivo);
			String urlImagen = MvcUriComponentsBuilder
					.fromMethodName(FicheroControlador.class, "serveFile", imagen, null).build().toUriString();
			personaje.setUrlImagen(urlImagen);
		}

		return personaje;
	}

	public Personaje editar(Long id, Personaje personaje, MultipartFile archivo) {

		if (existePorId(id)) {
			personaje.setId(id);

			if (!archivo.isEmpty()) {
				String imagen = almacenamientoServicio.guardar(archivo);
				String urlImagen = MvcUriComponentsBuilder
						.fromMethodName(FicheroControlador.class, "serveFile", imagen, null).build().toUriString();
				// Chequear el caso en que la imagen sea almacenada por primera vez con la
				// edicion.
				almacenamientoServicio.borrar(personaje.getUrlImagen());
				personaje.setUrlImagen(urlImagen);
			}

			return editar(personaje);
		} else
			return personaje;
	}

	public boolean existePorNombre(String nombre) {
		return this.repositorio.existsByNombre(nombre);
	}
}
