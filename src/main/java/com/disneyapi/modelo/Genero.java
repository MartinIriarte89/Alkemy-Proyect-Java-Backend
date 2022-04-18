package com.disneyapi.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Modela a los géneros de las películas y series.")
public class Genero {

	@ApiModelProperty(value = "Id del género de la película o serie.", dataType = "long", example = "1", allowableValues = "range[1,infinity]", required = true, position = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ApiModelProperty(value = "Nombre del género.", dataType = "long", example = "Animada", allowableValues = "range[1,50]", required = true, position = 2)
	@NotBlank
	@Column(unique = true)
	@Size(max = 50)
	private String nombre;
	
	@ApiModelProperty(value = "Url de la imagen del género.", dataType = "long", example = "http://localhost:8080/files/mi_imagen", required = false, position = 3)
	private String urlImagen;
}
