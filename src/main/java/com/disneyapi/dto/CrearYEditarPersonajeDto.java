package com.disneyapi.dto;

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
@ApiModel(description = "Necesario para crear un nuevo personaje o editar uno ya existente.")
public class CrearYEditarPersonajeDto {

	@ApiModelProperty(value = "Nombre del personaje.", dataType = "String", example = "Pocahontas", allowableValues = "range[1,50]", required = true, position = 1)
	@NotBlank
	@Size(max = 50)
	private String nombre;

	@ApiModelProperty(value = "Edad del personaje.", dataType = "int", example = "18", allowableValues = "range[0,200]", required = true, position = 2)
	@Min(0)
	@Max(200)
	@NotNull
	private int edad;

	@ApiModelProperty(value = "Peso del personaje.", dataType = "double", example = "60", allowableValues = "range[0,5000]", required = true, position = 3)
	@Min(0)
	@Max(5000)
	@NotNull
	private double peso;
	
	
	@ApiModelProperty(value = "Un resumen del transfondo del personaje.", dataType = "String", example = "Pocahontas es la hija de Powhatan, el jefe de una tribu india de América del Norte. Un día observa la llegada de un grupo de colones ingleses, encabezados por el ambicioso gobernador Radcliff y el valiente capitán John Smith. Acompañada de sus compañeros animales, Pocahontas entabla una amistad con el capitán John Smith. Sin embargo, la ambición de los colonos hace que surjan tensiones entre las dos culturas. Entonces Pocahontas deberá encontrar una manera de lograr la paz entre los dos bandos.", allowableValues = "range[infinity,1500]", required = false, position = 4)
	@Size(max = 1500)
	private String historia;
}
