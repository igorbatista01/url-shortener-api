package br.com.itau.url_shortener_api.exception;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import br.com.itau.url_shortener_api.dto.ErrorResponseDTO;
import br.com.itau.url_shortener_api.dto.ErrorResponseDTO.FieldError;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, WebRequest request) {
		
		List<FieldError> fieldErrors = new ArrayList<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> 
			fieldErrors.add(new FieldError(error.getField(), error.getDefaultMessage()))
		);

		ErrorResponseDTO errorResponse = new ErrorResponseDTO(
			"Erro de validação nos campos da requisição",
			HttpStatus.BAD_REQUEST.value(),
			"VALIDATION_ERROR",
			fieldErrors
		);

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleGlobalException(
			Exception ex, WebRequest request) {
		
		logger.error("Erro interno do servidor: {}", ex.getMessage(), ex);
		
		ErrorResponseDTO errorResponse = new ErrorResponseDTO(
			"Erro interno do servidor",
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			"INTERNAL_SERVER_ERROR"
		);

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
			ResourceNotFoundException ex, WebRequest request) {
		
		logger.warn("Tentativa de acesso a ID inexistente: {}", ex.getMessage());
		
		ErrorResponseDTO errorResponse = new ErrorResponseDTO(
			ex.getMessage(),
			HttpStatus.NOT_FOUND.value(),
			"RESOURCE_NOT_FOUND"
		);

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
}
