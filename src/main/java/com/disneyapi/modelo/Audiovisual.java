package com.disneyapi.modelo;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.disneyapi.util.serializador.AudiovisualSerializador;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Modela a las películas y las series", subTypes = {Pelicula.class, Serie.class})
public abstract class Audiovisual {

	@ApiModelProperty(value = "Id de la película o serie.", dataType = "long", example = "1", allowableValues = "range[1,infinity]", required = true, position = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ApiModelProperty(value = "Url de la imagen de la película o serie.", dataType = "String", example = "http://miweb.com/files/mi_imagen", required = false, position = 2)
	private String urlImagen;

	@ApiModelProperty(value = "Título de la película o serie.", dataType = "String", example = "Pocahontas", allowableValues = "range[1,150]", required = true, position = 3)
	@NotBlank
	@Size(max = 150)
	@Column(unique = true)
	private String titulo;

	@ApiModelProperty(value = "Fecha de lanzamiento de la película o serie.", dataType = "LocalDate", example = "1995-06-16", required = true, position = 4)
	@JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy")
	@NotNull
	private LocalDate fechaDeEstreno;

	@ApiModelProperty(value = "Calificación de la película o serie.", dataType = "double", example = "3.5", allowableValues = "range[0,5]", required = true, position = 5)
	@NotNull
	@Min(0)
	@Max(5)
	private double calificacion;

	@ApiModelProperty(value = "List de Personaje, con los personajes que pertenecen a la película o serie", dataType = "List", required = false, position = 6)
	@ManyToMany
	@JsonSerialize(using = AudiovisualSerializador.class)
	@JoinTable(joinColumns = @JoinColumn(name = "producto_id"), inverseJoinColumns = @JoinColumn(name = "personaje_id"))
	private List<Personaje> personajes;

	@ApiModelProperty(value = "Género al que pertenece la película o serie.", dataType = "object", required = true, position = 7)
	@ManyToOne
	@JoinColumn(name = "genero_id")
	@NotNull
	private Genero genero;

	public abstract boolean esNula();

	public void agregarA(Personaje personaje) {
		this.personajes.add(personaje);
	}

	public boolean contieneA(Personaje personaje) {
		return this.personajes.contains(personaje);
	}

	public void eliminarA(Personaje personaje) {
		this.personajes.remove(personaje);
	}
}
