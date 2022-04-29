package com.disneyapi.dto.usuario;

import javax.validation.constraints.Email;
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
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Envoltorio que se devuelve con el registro de un usuario, junto con su Jwt.")
public class GetUsuarioDto {

	@ApiModelProperty(value = "Nombre completo de la persona.", dataType = "String", example = "Raul Flores", allowableValues = "range[6,100]", required = true, position = 1)
	@NotBlank
	@Size(min = 6, max = 100)
	private String nombreCompleto;
	
	@ApiModelProperty(value = "Nick elegido para poder hacer el logueo. Debe ser unico", dataType = "String", example = "Raul2022", allowableValues = "range[4,20]", required = true, position = 2)
	@NotBlank
	@Size(min = 4, max = 20)
	private String nombreUsuario;
	
	@ApiModelProperty(value = "Email de la persona. Debe ser unico", dataType = "String", example = "raulflores@gmail.com", required = true, position = 3)
	@Email
	@NotNull
	private String email;
	
	@ApiModelProperty(value = "Token para poder autenticarse con los permisos que posee el usuario.", dataType = "String", example = "AJDSLKLA144646454ASDADS556-ASDDASD151ADS", required = true, position = 4)
	private String token;
}
