package com.disneyapi.controlador;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.disneyapi.error.ApiError;
import com.disneyapi.error.exception.AlmacenamientoArchivoNoEncontradoException;
import com.disneyapi.error.exception.AlmacenamientoException;
import com.disneyapi.error.exception.ContrasenasNoCoincidenException;
import com.disneyapi.error.exception.PersonajeNoEstaEnAudiovisualException;
import com.disneyapi.error.exception.PersonajeYaSeEncuentraException;
import com.disneyapi.error.exception.UsuarioNoEncontradoException;
import com.disneyapi.error.exception.UsuarioYaExisteException;
import com.disneyapi.error.exception.ValidacionException;

@RestControllerAdvice
public class ErrorControlador extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ AlmacenamientoArchivoNoEncontradoException.class, PersonajeNoEstaEnAudiovisualException.class,
			UsuarioNoEncontradoException.class, AlmacenamientoException.class})
	protected ResponseEntity<ApiError> exceptionNoEncontrado(RuntimeException exception) {
		return construirErrorResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler({ ContrasenasNoCoincidenException.class, PersonajeYaSeEncuentraException.class })
	protected ResponseEntity<ApiError> exceptionRequestIncorrecta(RuntimeException exception) {
		return construirErrorResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler({ UsuarioYaExisteException.class})
	protected ResponseEntity<ApiError> exceptionRequestEnConflicto(RuntimeException exception) {
		return construirErrorResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
	}
	
	@ExceptionHandler({ValidacionException.class})
	protected ResponseEntity<ApiError> exceptionRequestIncorrectaValidaciones(ValidacionException exception) {
		return construirErrorResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		ApiError apiError = new ApiError(status, ex.getMessage());

		return ResponseEntity.status(status).headers(headers).body(apiError);
	}

	private ResponseEntity<ApiError> construirErrorResponseEntity(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(ApiError.builder().estado(status).mensaje(message).build());
	}
}
