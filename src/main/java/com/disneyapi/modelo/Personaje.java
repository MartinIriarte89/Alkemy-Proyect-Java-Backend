package com.disneyapi.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.disneyapi.util.serializador.PersonajeSerializador;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Modela a los personajes.")
public class Personaje {

	@ApiModelProperty(value = "Id del personaje.", dataType = "long", example = "1", allowableValues = "range[1,infinity]", required = true, position = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ApiModelProperty(value = "Url de la imagen del personaje.", dataType = "String", example = "http://miweb.com/files/mi_imagen", required = false, position = 2)
	private String urlImagen;

	@ApiModelProperty(value = "Nombre del personaje.", dataType = "String", example = "Pocahontas",allowableValues = "range[1,50]" , required = true, position = 3)
	@NotBlank
	@Size(max = 50)
	@Column(unique = true)
	private String nombre;

	@ApiModelProperty(value = "Edad del personaje.", dataType = "int", example = "18", allowableValues = "range[0,200]", required = true, position = 4)
	@Min(0)
	@Max(200)
	@NotNull
	private int edad;

	@ApiModelProperty(value = "Peso del personaje.", dataType = "double", example = "60", allowableValues = "range[0,5000]", required = true, position = 5)
	@Min(0)
	@Max(5000)
	@NotNull
	private double peso;

	@ApiModelProperty(value = "Un resumen del transfondo del personaje.", dataType = "String", example = "Pocahontas es la hija de Powhatan, el jefe de una tribu india de América del Norte. Un día observa la llegada de un grupo de colones ingleses, encabezados por el ambicioso gobernador Radcliff y el valiente capitán John Smith. Acompañada de sus compañeros animales, Pocahontas entabla una amistad con el capitán John Smith. Sin embargo, la ambición de los colonos hace que surjan tensiones entre las dos culturas. Entonces Pocahontas deberá encontrar una manera de lograr la paz entre los dos bandos.", allowableValues = "range[infinity,1500]", required = false, position = 6)
	@Size(max = 1500)
	private String historia;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ApiModelProperty(value = "List de Audiovisual, que representan las películas y series en la que se encuentra el personaje.", dataType = "List", required = false, position = 7)
	@ManyToMany(mappedBy = "personajes", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JsonSerialize(using = PersonajeSerializador.class)
	private List<Audiovisual> audiovisuales = new ArrayList<>();

	public boolean esNulo() {
		return false;
	}
}
