package com.disneyapi.modelo;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("serie")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Serie extends Audiovisual {

	public Serie(String urlImagen, String titulo, LocalDate fechaDeCreacion, double calificacion,
			List<Personaje> personajes, Genero genero) {
		super(null, urlImagen, titulo, fechaDeCreacion, calificacion, personajes, genero);
	}
	
	public Serie(Long id, String urlImagen, String titulo, LocalDate fechaDeCreacion, double calificacion,
			List<Personaje> personajes, Genero genero) {
		super(id, urlImagen, titulo, fechaDeCreacion, calificacion, personajes, genero);
	}

	@Override
	public boolean esNula() {
		return false;
	}

}
