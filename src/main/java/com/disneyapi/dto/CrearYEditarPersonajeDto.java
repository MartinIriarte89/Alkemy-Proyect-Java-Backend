package com.disneyapi.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearYEditarPersonajeDto {
	
	@NotBlank
	@Size(max = 50)
	private String nombre;
	
	@Min(0)
	@Max(200)
	@NotNull
	private int edad;
	
	@Min(0)
	@Max(5000)
	@NotNull
	private double peso;
	
	private String historia;
}
