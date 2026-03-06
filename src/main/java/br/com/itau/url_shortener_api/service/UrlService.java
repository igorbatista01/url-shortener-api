package br.com.itau.url_shortener_api.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.itau.url_shortener_api.entity.UrlEntity;
import br.com.itau.url_shortener_api.repository.UrlRepository;

@Service
public class UrlService {

	private static final int SHORT_ID_LENGTH = 6;
	private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int MAX_RETRY_ATTEMPTS = 3;

	private final UrlRepository urlRepository;

	public UrlService(UrlRepository urlRepository) {
		this.urlRepository = urlRepository;
	}

	public UrlEntity createShortUrl(String originalUrl) {
		UrlEntity urlEntity = new UrlEntity();
		urlEntity.setOriginalUrl(originalUrl);
		urlEntity.setCreationDate(LocalDateTime.now());
		urlEntity.setClickCount(0L);


		int attempts = 0;
		while (attempts < MAX_RETRY_ATTEMPTS) {
			try {
				String shortId = generateUniqueShortId();
				urlEntity.setShortId(shortId);
				return urlRepository.save(urlEntity);
			} catch (DataIntegrityViolationException e) {
				attempts++;
				if (attempts >= MAX_RETRY_ATTEMPTS) {

					throw e;
				}
			}
		}

		//erro se tudo der ruim
		throw new DataIntegrityViolationException("falha  " + MAX_RETRY_ATTEMPTS + " tentativas");
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