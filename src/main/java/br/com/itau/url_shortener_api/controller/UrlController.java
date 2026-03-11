package br.com.itau.url_shortener_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.itau.url_shortener_api.dto.ShortenUrlRequestDTO;
import br.com.itau.url_shortener_api.dto.ShortenUrlResponseDTO;
import br.com.itau.url_shortener_api.entity.UrlEntity;
import br.com.itau.url_shortener_api.service.UrlService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/v1/urls")
public class UrlController {

	private static final String SHORT_URL_PREFIX = "http://localhost:8081/r/";

	private final UrlService urlService;

	public UrlController(UrlService urlService) {
		this.urlService = urlService;
	}


	@PostMapping
	public ResponseEntity<ShortenUrlResponseDTO> createShortenUrl(@Valid @RequestBody ShortenUrlRequestDTO requestDTO) {
		UrlEntity urlEntity = urlService.createShortUrl(requestDTO.getOriginalUrl(), requestDTO.getExpirationDate());
		
		ShortenUrlResponseDTO responseDTO = ShortenUrlResponseDTO.fromEntity(urlEntity, SHORT_URL_PREFIX);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
	}
}
