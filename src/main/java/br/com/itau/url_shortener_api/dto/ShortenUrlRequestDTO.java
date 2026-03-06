package br.com.itau.url_shortener_api.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.URL;


public class ShortenUrlRequestDTO {

	@NotBlank(message = "A URL original não pode estar em branco")
	//@URL(message = "A URL original deve ser uma URL válida")
	private String originalUrl;

	private LocalDateTime expirationDate;

	public ShortenUrlRequestDTO() {
	}

	public ShortenUrlRequestDTO(String originalUrl, LocalDateTime expirationDate) {
		this.originalUrl = originalUrl;
		this.expirationDate = expirationDate;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public String toString() {
		return "ShortenUrlRequestDTO [originalUrl=" + originalUrl + ", expirationDate=" + expirationDate + "]";
	}
}
