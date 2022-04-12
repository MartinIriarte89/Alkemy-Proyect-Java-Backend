package com.disneyapi.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Character {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String urlImagen;
	
	@NotBlank
	@Size(max = 60)
	@Column(unique = true)
	private String nombre;
	
	@Min(0)
	@Max(200)
	private int edad;
	
	@Min(0)
	@Max(5000)
	private double peso;
	
	private String historia;
	
	@ManyToMany
	@NotNull
	private List<Film> films;
}