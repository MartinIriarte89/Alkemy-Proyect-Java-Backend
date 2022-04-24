package com.disneyapi.dto.audiovisual;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class CrearAudiovisualDto {

	@ApiModelProperty(value = "indica si debe ser una pelicula o serie.", dataType = "String", example = "pelicula",allowableValues = "{pelicula, serie}", required = true, position = 1)
	@NotBlank
	private String tipo;
	
	@ApiModelProperty(value = "título de la película o serie, max 70 caracteres.", dataType = "String", example = "Pocahontas",allowableValues = "range(0,70)", required = true, position = 2)
	@NotBlank
	@Size(max = 70)
	private String titulo;

	@ApiModelProperty(value = "fecha de lanzamiento.", dataType = "LocalDate", example = "1995-06-16", required = true, position = 3)
	@NotNull
	private LocalDate fechaDeEstreno;
	
	@ApiModelProperty(value = "Calificación de la película o serie.", dataType = "double", example = "3.5",allowableValues = "range[0,5]" , required = true, position = 4)
	@NotNull
	@Min(0)
	@Max(5)
	private double calificacion;
	
	@ApiModelProperty(value = "Listado de ids tipo long que representan los personajes de la película o serie.", dataType = "List", example = "1", position = 5)
	private List<PersonajeIdDto> personajesPersonajeId;
	
	@ApiModelProperty(value = "Id del género de la película o serie.", dataType = "long", example = "1", required = true, position = 6)
	@NotNull
	@Min(1)
	private long generoId;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ApiModel
	public static class PersonajeIdDto{
		
		@NotNull
		@Min(1)
		@ApiModelProperty(value = "Id del personje de la película o serie.", dataType = "long", example = "1", required = true)
		private Long id;
	}
}
