package com.disneyapi.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Necesario para poder llevar a cabo el registro de un usuario.")
public class UsuarioRegistroDto {

	@ApiModelProperty(value = "Nombre completo del usuario.", dataType = "String", example = "Raul Flores", allowableValues = "range[6,100]", required = true, position = 1)
	@NotBlank
	@Size(min = 6, max = 100)
	private String nombreCompleto;
	
	@ApiModelProperty(value = "Nick elegido para poder hacer el logueo. Debe ser unico", dataType = "String", example = "Raul2022", allowableValues = "range[4,20]", required = true, position = 2)
	@NotBlank
	@Size(min = 4, max = 20)
	private String nombreUsuario;
	
	@ApiModelProperty(value = "Contraseña del usuario para poder loguearse.", dataType = "String", example = "Raul45698", allowableValues = "range[5,15]", required = true, position = 3)
	@NotBlank
	@Size(min = 5, max = 15)
	private String contrasena;
	
	@ApiModelProperty(value = "Repetición de la contraseña elegida para poder hacer el chequeo de la misma.", dataType = "String", example = "Raul45698", allowableValues = "range[5,15]", required = true, position = 4)
	@NotBlank
	@Size(min = 5, max = 15)
	private String contrasenaRepetida;
	
	@ApiModelProperty(value = "Email de la persona. Debe ser unico", dataType = "String", example = "raulflores@gmail.com", required = true, position = 5)
	@NotNull
	@Email
	private String email;
}
