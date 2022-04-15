package com.disneyapi.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDto {

	@NotBlank
	@Size(min = 6, max = 100)
	private String nombreCompleto;
	
	@NotBlank
	@Size(min = 4, max = 20)
	private String nombreUsuario;
	
	@NotBlank
	@Size(min = 5, max = 15)
	private String contrasena;
	
	@NotBlank
	@Size(min = 5, max = 15)
	private String contrasenaRepetida;
}
