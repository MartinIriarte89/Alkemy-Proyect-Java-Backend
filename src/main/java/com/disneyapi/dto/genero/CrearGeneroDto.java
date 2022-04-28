package com.disneyapi.dto.genero;

import javax.validation.constraints.NotBlank;
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
@ApiModel(description = "Necesario para crear un nuevo género.")
public class CrearGeneroDto {

	@ApiModelProperty(value = "Nombre del género.", dataType = "String", example = "Animada", allowableValues = "range[1,50]", required = true ,position = 1)
	@NotBlank
	@Size(max = 50)
	private String nombre;
}
