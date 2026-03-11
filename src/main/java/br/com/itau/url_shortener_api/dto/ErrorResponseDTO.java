package br.com.itau.url_shortener_api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponseDTO {

	private String message;
	private int statusCode;
	private String internalCode;
	private LocalDateTime timestamp;
	private List<FieldError> errors;

	public ErrorResponseDTO() {
	}

	public ErrorResponseDTO(String message, int statusCode, String internalCode) {
		this.message = message;
		this.statusCode = statusCode;
		this.internalCode = internalCode;
		this.timestamp = LocalDateTime.now();
	}

	public ErrorResponseDTO(String message, int statusCode, String internalCode, List<FieldError> errors) {
		this.message = message;
		this.statusCode = statusCode;
		this.internalCode = internalCode;
		this.timestamp = LocalDateTime.now();
		this.errors = errors;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getInternalCode() {
		return internalCode;
	}

	public void setInternalCode(String internalCode) {
		this.internalCode = internalCode;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public List<FieldError> getErrors() {
		return errors;
	}

	public void setErrors(List<FieldError> errors) {
		this.errors = errors;
	}

	// Classe interna para erros de campo
	public static class FieldError {
		private String field;
		private String message;

		public FieldError(String field, String message) {
			this.field = field;
			this.message = message;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	@Override
	public String toString() {
		return "ErrorResponseDTO [message=" + message + ", statusCode=" + statusCode + ", internalCode=" + internalCode
				+ ", timestamp=" + timestamp + ", errors=" + errors + "]";
	}
}
