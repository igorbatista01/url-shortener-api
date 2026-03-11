package br.com.itau.url_shortener_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.itau.url_shortener_api.entity.UrlEntity;
import br.com.itau.url_shortener_api.repository.UrlRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - UrlService")
class UrlServiceTest {

	@Mock
	private UrlRepository urlRepository;

	@InjectMocks
	private UrlService urlService;

	private String testUrl;

	@BeforeEach
	void setUp() {
		testUrl = "https://www.example.com";
	}

	@Test
	@DisplayName("Deve criar uma URL curta com sucesso")
	void testCreateShortUrlSuccess() {

		when(urlRepository.findByShortId(anyString())).thenReturn(java.util.Optional.empty());
		
		UrlEntity savedEntity = new UrlEntity();
		savedEntity.setId(1L);
		savedEntity.setOriginalUrl(testUrl);
		savedEntity.setShortId("abc123");
		savedEntity.setClickCount(0L);
		when(urlRepository.save(any(UrlEntity.class))).thenReturn(savedEntity);

		LocalDateTime expirationDate = LocalDateTime.now().plusDays(30);
		UrlEntity result = urlService.createShortUrl(testUrl, expirationDate);


		assertNotNull(result, "O resultado não deve ser nulo");
		assertEquals(testUrl, result.getOriginalUrl(), "URL original deve ser preservada");
		assertEquals(0L, result.getClickCount(), "clickCount deve começar em 0");
		assertNotNull(result.getShortId(), "shortId deve ser gerado");
		
		verify(urlRepository, times(1)).save(any(UrlEntity.class));
		verify(urlRepository, atLeast(1)).findByShortId(anyString());
	}

	@Test
	@DisplayName("Deve definir createdAt durante a criação")
	void testCreateShortUrlSetsCreatedAt() {
		when(urlRepository.findByShortId(anyString())).thenReturn(java.util.Optional.empty());
		
		UrlEntity savedEntity = new UrlEntity();
		savedEntity.setId(1L);
		savedEntity.setOriginalUrl(testUrl);
		savedEntity.setShortId("abc123");
		savedEntity.setClickCount(0L);
		when(urlRepository.save(any(UrlEntity.class))).thenReturn(savedEntity);

		LocalDateTime beforeTest = LocalDateTime.now();
		LocalDateTime expirationDate = LocalDateTime.now().plusDays(30);
		UrlEntity result = urlService.createShortUrl(testUrl, expirationDate);
		LocalDateTime afterTest = LocalDateTime.now();
		assertNotNull(result, "Resultado não deve ser nulo");
		assertTrue(beforeTest.isBefore(result.getCreatedAt()) || 
		           beforeTest.isEqual(result.getCreatedAt()),
		           "createdAt deve ser >= ao momento do teste");
		assertTrue(afterTest.isAfter(result.getCreatedAt()) || 
		           afterTest.isEqual(result.getCreatedAt()),
		           "createdAt deve ser <= ao momento do teste");
	}

	@Test
	@DisplayName("Deve capturar e validar a entidade salva")
	void testCreateShortUrlCaptureSavedEntity() {

		when(urlRepository.findByShortId(anyString())).thenReturn(java.util.Optional.empty());
		
		UrlEntity savedEntity = new UrlEntity();
		savedEntity.setId(1L);
		savedEntity.setOriginalUrl(testUrl);
		savedEntity.setShortId("xyz789");
		savedEntity.setClickCount(0L);
		when(urlRepository.save(any(UrlEntity.class))).thenReturn(savedEntity);


		LocalDateTime expirationDate = LocalDateTime.now().plusDays(30);
		UrlEntity result = urlService.createShortUrl(testUrl, expirationDate);

	
		ArgumentCaptor<UrlEntity> captor = ArgumentCaptor.forClass(UrlEntity.class);
		verify(urlRepository).save(captor.capture());
		
		UrlEntity capturedEntity = captor.getValue();
		assertEquals(testUrl, capturedEntity.getOriginalUrl(), 
		             "URL original capturada deve ser igual à fornecida");
		assertEquals(0L, capturedEntity.getClickCount(), 
		             "clickCount deve ser 0");
		assertNotNull(capturedEntity.getShortId(), 
		              "shortId deve ter sido gerado");
	}

	@Test
	@DisplayName("Deve retentar ao encontrar ID duplicado")
	void testCreateShortUrlRetryOnDuplicateId() {

		when(urlRepository.findByShortId(anyString()))
			.thenReturn(java.util.Optional.of(new UrlEntity()))
			.thenReturn(java.util.Optional.empty()); 
		
		UrlEntity savedEntity = new UrlEntity();
		savedEntity.setId(1L);
		savedEntity.setOriginalUrl(testUrl);
		savedEntity.setShortId("unique123");
		savedEntity.setClickCount(0L);
		when(urlRepository.save(any(UrlEntity.class))).thenReturn(savedEntity);


		LocalDateTime expirationDate = LocalDateTime.now().plusDays(30);
		UrlEntity result = urlService.createShortUrl(testUrl, expirationDate);

		assertNotNull(result, "Resultado deve ser válido mesmo após retry");
		verify(urlRepository, times(2)).findByShortId(anyString());
		verify(urlRepository, times(1)).save(any(UrlEntity.class));
	}

	@Test
	@DisplayName("Deve redirecionar com sucesso e incrementar clickCount")
	void testRedirectToUrlSuccess() {
		// Arrange
		String shortId = "abc123";
		UrlEntity existingEntity = new UrlEntity();
		existingEntity.setId(1L);
		existingEntity.setOriginalUrl(testUrl);
		existingEntity.setShortId(shortId);
		existingEntity.setClickCount(0L);
		
		when(urlRepository.findByShortId(shortId))
			.thenReturn(java.util.Optional.of(existingEntity));
		
		UrlEntity updatedEntity = new UrlEntity();
		updatedEntity.setId(1L);
		updatedEntity.setOriginalUrl(testUrl);
		updatedEntity.setShortId(shortId);
		updatedEntity.setClickCount(1L);
		when(urlRepository.save(any(UrlEntity.class))).thenReturn(updatedEntity);


		UrlEntity result = urlService.redirectToUrl(shortId);

	
		assertNotNull(result, "Resultado não deve ser nulo");
		assertEquals(testUrl, result.getOriginalUrl(), "URL original deve ser retornada");
		assertEquals(1L, result.getClickCount(), "clickCount deve ter incrementado de 0 para 1");
		

		verify(urlRepository, times(1)).findByShortId(shortId);
		verify(urlRepository, times(1)).save(any(UrlEntity.class));
	}

	@Test
	@DisplayName("Deve incrementar clickCount corretamente")
	void testRedirectToUrlIncrementsClickCount() {

		String shortId = "xyz789";
		UrlEntity existingEntity = new UrlEntity();
		existingEntity.setId(1L);
		existingEntity.setOriginalUrl(testUrl);
		existingEntity.setShortId(shortId);
		existingEntity.setClickCount(5L); 
		
		when(urlRepository.findByShortId(shortId))
			.thenReturn(java.util.Optional.of(existingEntity));
		
		UrlEntity updatedEntity = new UrlEntity();
		updatedEntity.setId(1L);
		updatedEntity.setOriginalUrl(testUrl);
		updatedEntity.setShortId(shortId);
		updatedEntity.setClickCount(6L);
		when(urlRepository.save(any(UrlEntity.class))).thenReturn(updatedEntity);

		UrlEntity result = urlService.redirectToUrl(shortId);

		assertEquals(6L, result.getClickCount(), "clickCount deve ter incrementado de 5 para 6");
		
		ArgumentCaptor<UrlEntity> captor = ArgumentCaptor.forClass(UrlEntity.class);
		verify(urlRepository).save(captor.capture());
		assertEquals(6L, captor.getValue().getClickCount(), 
		             "Entidade capturada deve ter clickCount = 6");
	}

	@Test
	@DisplayName("Deve lançar ResourceNotFoundException quando URL não existe")
	void testRedirectToUrlNotFound() {

		String shortId = "naoexiste";
		when(urlRepository.findByShortId(shortId))
			.thenReturn(java.util.Optional.empty());


		Exception exception = assertThrows(
			br.com.itau.url_shortener_api.exception.ResourceNotFoundException.class,
			() -> urlService.redirectToUrl(shortId),
			"Deve lançar ResourceNotFoundException"
		);

		assertTrue(exception.getMessage().contains(shortId), 
		           "Mensagem deve conter o shortId");
		assertTrue(exception.getMessage().contains("não encontrada"), 
		           "Mensagem deve indicar que não foi encontrada");
		

		verify(urlRepository, times(1)).findByShortId(shortId);
		verify(urlRepository, never()).save(any(UrlEntity.class)); 
	}

	@Test
	@DisplayName("Não deve chamar save() se URL não existe")
	void testRedirectToUrlDoesNotSaveIfNotFound() {

		String shortId = "invalido";
		when(urlRepository.findByShortId(shortId))
			.thenReturn(java.util.Optional.empty());

		assertThrows(br.com.itau.url_shortener_api.exception.ResourceNotFoundException.class,
		             () -> urlService.redirectToUrl(shortId));


		verify(urlRepository, never()).save(any(UrlEntity.class));
	}

}
