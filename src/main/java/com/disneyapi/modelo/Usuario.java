package com.disneyapi.modelo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.disneyapi.util.enumerados.RolUsuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 6, max = 100)
	private String nombreCompleto;

	@NotBlank
	@Column(unique = true)
	@Size(min = 4, max = 20)
	private String nombreUsuario;

	@NotBlank
	@Size(min = 5)
	private String contrasena;
	
	@Email
	@NotNull
	private String email;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private List<RolUsuario> roles;
	
	public boolean esNulo() {
		return false;
	}
}
