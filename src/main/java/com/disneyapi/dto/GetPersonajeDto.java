package com.disneyapi.dto;

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
public class GetPersonajeDto {

	@NotNull
	private String urlImagen;
	
	@NotBlank
	@Size(max = 50)
	private String nombre;
}
