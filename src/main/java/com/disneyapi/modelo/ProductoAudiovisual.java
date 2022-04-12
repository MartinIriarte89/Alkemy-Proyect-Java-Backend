package com.disneyapi.modelo;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class ProductoAudiovisual {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String urlImagen;
	
	@NotBlank
	@Size(max = 70)
	private String titulo;
	
	@NotNull
	private LocalDateTime fechaDeCreacion;
	
	@NotNull
	@Min(0)
	@Max(5)
	private double calificacion;
	
	@ManyToMany
	@JoinTable(
			joinColumns = @JoinColumn(name = "producto_id"),
			inverseJoinColumns = @JoinColumn(name = "personaje_id"))
	private List<Personaje> personajes;
}