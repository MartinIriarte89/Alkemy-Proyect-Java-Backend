package com.disneyapi.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearYEditarAudiovisualDto {

	private String tipo;
	
	private String titulo;
	
	private LocalDate fechaDeCreacion;
	
	private double calificacion;
	
	private List<PersonajeIdDto> personajesPersonajeId;
	
	private long generoId;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PersonajeIdDto{
		
		private Long id;
	}
}
