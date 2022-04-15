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
public class UsuarioLoginDto {

	@NotBlank
	@Size(min = 4, max = 20)
	private String username;
	
	@NotBlank
	@Size(min = 5, max = 15)
	private String password;
}
