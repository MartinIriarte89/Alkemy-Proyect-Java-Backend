package com.disneyapi.modelo;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Audiovisual {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String urlImagen;
	
	@NotBlank
	@Size(max = 70)
	@Column(unique = true)
	private String titulo;
	
	@NotNull
	private LocalDate fechaDeCreacion;
	
	@NotNull
	@Min(0)
	@Max(5)
	private double calificacion;
	
	@ManyToMany
	@JoinTable(
			joinColumns = @JoinColumn(name = "producto_id"),
			inverseJoinColumns = @JoinColumn(name = "personaje_id"))
	private List<Personaje> personajes;
	
	@ManyToOne
	@JoinColumn(name = "genero_id")
	@NotNull
	private Genero genero;
	
	public abstract boolean esNula();
}