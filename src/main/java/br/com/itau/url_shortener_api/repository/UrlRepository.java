package br.com.itau.url_shortener_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.itau.url_shortener_api.entity.UrlEntity;

public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
	Optional<UrlEntity> findByShortId(String shortId);

}
