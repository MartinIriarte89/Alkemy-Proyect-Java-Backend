package com.disneyapi.dto;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetUsuarioDto {

	@NotBlank
	@Size(min = 6, max = 100)
	private String nombreCompleto;
	
	@NotBlank
	@Column(unique = true)
	@Size(min = 4, max = 20)
	private String nombreUsuario;
	
	@Email
	@NotNull
	private String email;
	
	private String token;
}
