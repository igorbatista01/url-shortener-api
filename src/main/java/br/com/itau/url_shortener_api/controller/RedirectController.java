package br.com.itau.url_shortener_api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.itau.url_shortener_api.entity.UrlEntity;
import br.com.itau.url_shortener_api.exception.ResourceNotFoundException;
import br.com.itau.url_shortener_api.service.UrlService;


@RestController
@RequestMapping("/r")
public class RedirectController {

	private final UrlService urlService;

	public RedirectController(UrlService urlService) {
		this.urlService = urlService;
	}


	@GetMapping("/{shortId}")
	public ResponseEntity<Void> redirect(@PathVariable String shortId) {
		UrlEntity urlEntity = urlService.redirectToUrl(shortId);
		
		//Retorna redirecionamento com status 302 (FOUND) ,
		HttpHeaders headers = new HttpHeaders();
		headers.set("Location", urlEntity.getOriginalUrl());
		
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
}
