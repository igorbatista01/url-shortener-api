package br.com.itau.url_shortener_api.dto;

import java.time.LocalDateTime;

import br.com.itau.url_shortener_api.entity.UrlEntity;

public class ShortenUrlResponseDTO {

	private String id;
	private String originalUrl;
	private String shortUrl;
	private LocalDateTime createdAt;
	private LocalDateTime expirationDate;
	private Long clickCount;

	public ShortenUrlResponseDTO() {
	}

	public ShortenUrlResponseDTO(String id, String originalUrl, String shortUrl, 
			LocalDateTime createdAt, LocalDateTime expirationDate, Long clickCount) {
		this.id = id;
		this.originalUrl = originalUrl;
		this.shortUrl = shortUrl;
		this.createdAt = createdAt;
		this.expirationDate = expirationDate;
		this.clickCount = clickCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
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
	
	public static ShortenUrlResponseDTO fromEntity(UrlEntity urlEntity, String shortUrl) {
	    return new ShortenUrlResponseDTO(
	        urlEntity.getShortId(),
	        urlEntity.getOriginalUrl(),
	        shortUrl + urlEntity.getShortId(),
	        urlEntity.getCreatedAt(),
	        urlEntity.getExpirationDate(),
	        urlEntity.getClickCount()
	    );
	}

	@Override
	public String toString() {
		return "ShortenUrlResponseDTO [id=" + id + ", originalUrl=" + originalUrl
				+ ", shortUrl=" + shortUrl + ", createdAt=" + createdAt + ", expirationDate=" + expirationDate
				+ ", clickCount=" + clickCount + "]";
	}
}
