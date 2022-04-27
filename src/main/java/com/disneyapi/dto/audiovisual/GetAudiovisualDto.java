package com.disneyapi.dto.audiovisual;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Envoltorio que se devuelve para trabajar con los datos básicos de una película o serie.")
public class GetAudiovisualDto {

	@ApiModelProperty(value = "Url de la imagen de la película o serie.", dataType = "String", example = "http://miweb.com/files/mi_imagen", required = false, position = 1)
	private String urlImagen;
	
	@ApiModelProperty(value = "Título de la película o serie.", dataType = "String", example = "Pocahontas", allowableValues = "range[1,150]", required = true, position = 2)
	@NotBlank
	@Size(max = 150)
	private String titulo;
	
	@ApiModelProperty(value = "Fecha de lanzamiento de la película o serie.", dataType = "LocalDate", example = "1995-06-16", required = true, position = 3)
	@NotNull
	private LocalDate fechaDeEstreno;
}
