package com.disneyapi.dto.personaje;

import javax.validation.constraints.NotBlank;
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
@ApiModel(description = "Envoltorio que se devuelve para trabajar con los datos básicos de un personaje.")
public class GetPersonajeDto {

	@ApiModelProperty(value = "Url de la imagen del personaje.", dataType = "String", example = "http://miweb.com/files/mi_imagen", required = false, position = 1)
	private String urlImagen;
	
	@ApiModelProperty(value = "Nombre del personaje.", dataType = "String", example = "Pocahontas",allowableValues = "range[1,50]" , required = true, position = 2)
	@NotBlank
	@Size(max = 50)
	private String nombre;
}
