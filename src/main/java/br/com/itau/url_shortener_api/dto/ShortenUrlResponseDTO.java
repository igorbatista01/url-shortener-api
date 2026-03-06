package br.com.itau.url_shortener_api.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta de encurtamento de URL.
 * 
 * Campos:
 * - id: Identificador único da URL encurtada
 * - originalUrl: URL original que foi encurtada
 * - shortId: ID único de 6 caracteres para a URL encurtada
 * - shortUrl: URL encurtada completa (ex: http://localhost:8080/r/XyZ123)
 * - creationDate: Data de criação da URL encurtada
 * - expirationDate: Data de expiração (pode ser nula)
 * - clickCount: Número de cliques/acessos à URL encurtada
 */
public class ShortenUrlResponseDTO {

	private Long id;
	private String originalUrl;
	private String shortId;
	private String shortUrl;
	private LocalDateTime creationDate;
	private LocalDateTime expirationDate;
	private Long clickCount;

	public ShortenUrlResponseDTO() {
	}

	public ShortenUrlResponseDTO(Long id, String originalUrl, String shortId, String shortUrl, 
			LocalDateTime creationDate, LocalDateTime expirationDate, Long clickCount) {
		this.id = id;
		this.originalUrl = originalUrl;
		this.shortId = shortId;
		this.shortUrl = shortUrl;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
		this.clickCount = clickCount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getShortId() {
		return shortId;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Long getClickCount() {
		return clickCount;
	}

	public void setClickCount(Long clickCount) {
		this.clickCount = clickCount;
	}

	@Override
	public String toString() {
		return "ShortenUrlResponseDTO [id=" + id + ", originalUrl=" + originalUrl + ", shortId=" + shortId
				+ ", shortUrl=" + shortUrl + ", creationDate=" + creationDate + ", expirationDate=" + expirationDate
				+ ", clickCount=" + clickCount + "]";
	}
}
