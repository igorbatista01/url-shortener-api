package br.com.itau.url_shortener_api.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.itau.url_shortener_api.entity.UrlEntity;
import br.com.itau.url_shortener_api.repository.UrlRepository;

@Service
public class UrlService {

	private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
	private static final int SHORT_ID_LENGTH = 6;
	private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int MAX_RETRY_ATTEMPTS = 3;

	private final UrlRepository urlRepository;

	public UrlService(UrlRepository urlRepository) {
		this.urlRepository = urlRepository;
	}

	public UrlEntity createShortUrl(String originalUrl, LocalDateTime expirationDate) {
		UrlEntity urlEntity = new UrlEntity();
		urlEntity.setOriginalUrl(originalUrl);
		urlEntity.setExpirationDate(expirationDate);
		urlEntity.setCreatedAt(LocalDateTime.now());
		urlEntity.setClickCount(0L);


		int attempts = 0;
		while (attempts < MAX_RETRY_ATTEMPTS) {
			try {
				String shortId = generateUniqueShortId();
				urlEntity.setShortId(shortId);
				UrlEntity savedUrl = urlRepository.save(urlEntity);
				logger.info("URL encurtada criada com sucesso. Original: {}, ShortId: {}", originalUrl, shortId);
				return savedUrl;
			} catch (DataIntegrityViolationException e) {
				attempts++;
				if (attempts >= MAX_RETRY_ATTEMPTS) {
					logger.error("Falha ao criar URL encurtada após {} tentativas para URL: {}", MAX_RETRY_ATTEMPTS, originalUrl);
					throw e;
				}
			}
		}

		throw new DataIntegrityViolationException("falha  " + MAX_RETRY_ATTEMPTS + " tentativas");
	}


	public UrlEntity redirectToUrl(String shortId) {
		UrlEntity urlEntity = urlRepository.findByShortId(shortId)
			.orElseThrow(() -> new br.com.itau.url_shortener_api.exception.ResourceNotFoundException(
				"URL com ID '" + shortId + "' não encontrada"));
		
		if (urlEntity.getExpirationDate() != null && urlEntity.getExpirationDate().isBefore(LocalDateTime.now())) {
			logger.warn("Tentativa de acesso a URL expirada: ID {}", shortId);
			throw new br.com.itau.url_shortener_api.exception.ResourceNotFoundException(
				"Esta URL encurtada já expirou e não está mais disponível.");
		}
		
		urlEntity.setClickCount(urlEntity.getClickCount() + 1);
		return urlRepository.save(urlEntity);
	}

	private String generateUniqueShortId() {
		String shortId = "";
		boolean exists = true;

		while (exists) {
			shortId = RandomStringUtils.random(SHORT_ID_LENGTH, ALLOWED_CHARACTERS);
			exists = urlRepository.findByShortId(shortId).isPresent();
		}

		return shortId;
	}
}