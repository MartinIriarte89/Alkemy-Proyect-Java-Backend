package com.disneyapi.dto;

import java.time.LocalDate;

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
@AllArgsConstructor
@NoArgsConstructor
public class EditarAudiovisualDto {
	
	@NotBlank
	@Size(max = 70)
	private String titulo;
	
	@NotNull
	private LocalDate fechaDeEstreno;
	
	@NotNull
	@Min(0)
	@Max(5)
	private double calificacion;
	
	@NotNull
	@Min(1)
	private long generoId;
}
