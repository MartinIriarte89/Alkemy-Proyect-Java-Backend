package com.disneyapi.servicio;

import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.disneyapi.modelo.Audiovisual;
import com.disneyapi.repositorio.AudiovisualRepositorio;
import com.disneyapi.servicio.base.BaseServicio;

@Service
public class AudiovisualServicio extends BaseServicio<Audiovisual, Long, AudiovisualRepositorio> {

	private AlmacenamientoServicio almacenamientoServicio;

	@Autowired
	public AudiovisualServicio(AudiovisualRepositorio repositorio, AlmacenamientoServicio almacenamientoServicio) {
		super(repositorio);
		this.almacenamientoServicio = almacenamientoServicio;
	}

	public Optional<Audiovisual> buscarPorTitulo(String titulo) {
		return this.repositorio.findByTitulo(titulo);
	}

	public Page<Audiovisual> buscarPorArgs(Optional<String> genero, Optional<String> orden, Pageable pageable) {

		Specification<Audiovisual> specGeneroAudiovisual = new Specification<Audiovisual>() {
			
			private static final long serialVersionUID = -1901426456386321439L;

			@Override
			public Predicate toPredicate(Root<Audiovisual> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				if (genero.isPresent()) {
					return criteriaBuilder.equal(root.get("genero").get("nombre"), genero.get());
				} else {
					return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
				}
			}
		};

		if (orden.isPresent()) {
			if (orden.get().equalsIgnoreCase("ASC"))
				pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
						Sort.by(Sort.Order.asc("fechaDeCreacion")));
			else if (orden.get().equalsIgnoreCase("DESC"))
				pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
						Sort.by(Sort.Order.desc("fechaDeCreacion")));
		}
		
		return this.repositorio.findAll(specGeneroAudiovisual, pageable);
	}

	public Audiovisual guardarImagenYAgregarUrlImagen(Audiovisual convertirCrearYEditarAudiovisualDtoAAudiovisual,
			MultipartFile imagen) {
		
		return null;
	}

	public Audiovisual editar(Long id, Audiovisual convertirCrearYEditarAudiovisualDtoAAudiovisual,
			MultipartFile imagen) {
		return null;
	}
	
}
