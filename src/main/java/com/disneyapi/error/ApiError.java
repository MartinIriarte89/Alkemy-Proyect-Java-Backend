package com.disneyapi.error;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "Modela los errores que serán enviados al front en caso de que ocurran")
public class ApiError {

	@ApiModelProperty(value = "Estado del error que se envia", dataType = "HttpStatus", example = "404", required = true, position = 1)
	@NonNull
	private HttpStatus estado;

	@ApiModelProperty(value = "Fecha y hora en la que se genero el error. Se asigna por defecto.", dataType = "LocalDate", example = "2022-04-18", required = false, position = 2)
	@Builder.Default
	@JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
	private LocalDateTime fecha = LocalDateTime.now();

	@ApiModelProperty(value = "Mensaje del error ocurrido.", dataType = "String", required = true, position = 3)
	@NonNull
	private String mensaje;
	
	@ApiModelProperty(value = "Lista de errores detectados.", dataType = "ObjectError", required = false, position = 4)
	private List<ObjectError> errores;
}
