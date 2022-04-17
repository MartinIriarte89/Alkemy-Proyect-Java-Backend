package com.disneyapi.dto;

import java.time.LocalDate;
import java.util.List;

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
public class CrearAudiovisualDto {

	private String tipo;
	
	@NotBlank
	@Size(max = 70)
	private String titulo;
	
	@NotNull
	private LocalDate fechaDeEstreno;
	
	@NotNull
	@Min(0)
	@Max(5)
	private double calificacion;
	
	private List<PersonajeIdDto> personajesPersonajeId;
	
	@NotNull
	@Min(1)
	private long generoId;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PersonajeIdDto{
		
		@NotNull
		@Min(1)
		private Long id;
	}
}
