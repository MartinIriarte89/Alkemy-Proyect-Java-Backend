package com.disneyapi.dto;

import java.time.LocalDate;

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
public class GetAudiovisualDto {

	private String urlImagen;
	
	@NotBlank
	@Size(max = 150)
	private String titulo;
	
	@NotNull
	private LocalDate fechaDeCreacion;
}
