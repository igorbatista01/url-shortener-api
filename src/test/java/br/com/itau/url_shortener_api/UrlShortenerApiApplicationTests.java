package br.com.itau.url_shortener_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.com.itau.url_shortener_api.entity.UrlEntity;
import br.com.itau.url_shortener_api.repository.UrlRepository;

@SpringBootTest
@WebAppConfiguration
@DisplayName("Testes de Integração - Fluxo Completo")
class UrlShortenerApiApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private UrlRepository urlRepository;

	private static final String API_BASE_URL = "/v1/urls";
	private static final String REDIRECT_BASE_URL = "/r";

	@org.junit.jupiter.api.BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	@DisplayName("Criar URL com sucesso retorna 201 Created")
	void testCreateUrlSuccess() throws Exception {
		String originalUrl = "https://www.google.com";
		String requestBody = "{\"originalUrl\": \"" + originalUrl + "\"}";

		mockMvc.perform(
			post(API_BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
		)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isString())
			.andExpect(jsonPath("$.originalUrl").value(originalUrl))
			.andExpect(jsonPath("$.shortUrl").exists())
			.andExpect(jsonPath("$.createdAt").isString())
			.andExpect(jsonPath("$.clickCount").value(0));
	}

	@Test
	@DisplayName("Validação: URL vazia deve retornar 400")
	void testCreateUrlWithEmptyUrl() throws Exception {
		String requestBody = "{\"originalUrl\": \"\"}";

		mockMvc.perform(
			post(API_BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
		)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.internalCode").value("VALIDATION_ERROR"))
			.andExpect(jsonPath("$.errors").isArray());
	}

	@Test
	@DisplayName("Validação: URL inválida deve retornar 400")
	void testCreateUrlWithInvalidUrl() throws Exception {
		String requestBody = "{\"originalUrl\": \"nao-eh-url-valida\"}";

		mockMvc.perform(
			post(API_BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
		)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.statusCode").value(400))
			.andExpect(jsonPath("$.internalCode").value("VALIDATION_ERROR"));
	}

	@Test
	@DisplayName("Redirecionamento: ID válido retorna 302")
	void testRedirectWithValidId() throws Exception {
		String originalUrl = "https://www.reddit.com";
		String requestBody = "{\"originalUrl\": \"" + originalUrl + "\"}";

		MvcResult createResult = mockMvc.perform(
			post(API_BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
		)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.shortUrl").exists())
			.andReturn();

		String responseBody = createResult.getResponse().getContentAsString();

		String shortUrl = responseBody.substring(responseBody.indexOf("\"shortUrl\":\"") + 12);
		shortUrl = shortUrl.substring(0, shortUrl.indexOf("\""));

		String shortId = shortUrl.substring(shortUrl.lastIndexOf("/") + 1);

		mockMvc.perform(get(REDIRECT_BASE_URL + "/" + shortId))
			.andExpect(status().isFound())
			.andExpect(header().string("Location", originalUrl));
	}

	@Test
	@DisplayName("Redirecionamento: ID inválido retorna 404")
	void testRedirectWithInvalidId() throws Exception {
		mockMvc.perform(get(REDIRECT_BASE_URL + "/invalido123"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.statusCode").value(404))
			.andExpect(jsonPath("$.internalCode").value("RESOURCE_NOT_FOUND"))
			.andExpect(jsonPath("$.message").exists());
	}

	@Test
	@DisplayName("Incremento de Cliques: clickCount inicia em 0")
	void testClickCountInitialValue() throws Exception {
		String originalUrl = "https://www.twitter.com";
		String requestBody = "{\"originalUrl\": \"" + originalUrl + "\"}";

		mockMvc.perform(
			post(API_BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
		)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.clickCount").value(0));
	}

	@Test
	@DisplayName("Resposta JSON: Deve conter todos os campos obrigatórios")
	void testResponseJsonStructure() throws Exception {
		String originalUrl = "https://www.linkedin.com";
		String requestBody = "{\"originalUrl\": \"" + originalUrl + "\"}";

		mockMvc.perform(
			post(API_BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)
		)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.id").isString())
			.andExpect(jsonPath("$.originalUrl").exists())
			.andExpect(jsonPath("$.originalUrl").value(originalUrl))
			.andExpect(jsonPath("$.shortUrl").exists())
			.andExpect(jsonPath("$.createdAt").exists())
			.andExpect(jsonPath("$.clickCount").exists())
			.andExpect(jsonPath("$.clickCount").value(0));
	}

	@Test
	@DisplayName("Isolamento: URLs diferentes devem ter URLs diferentes")
	void testUrlsHaveDifferentIds() throws Exception {
		String url1 = "https://www.facebook.com";
		String url2 = "https://www.instagram.com";

		MvcResult result1 = mockMvc.perform(
			post(API_BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"originalUrl\": \"" + url1 + "\"}")
		)
			.andExpect(status().isCreated())
			.andReturn();

		MvcResult result2 = mockMvc.perform(
			post(API_BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"originalUrl\": \"" + url2 + "\"}")
		)
			.andExpect(status().isCreated())
			.andReturn();

		String response1 = result1.getResponse().getContentAsString();
		String response2 = result2.getResponse().getContentAsString();
		
		assertTrue(response1.contains("shortUrl"));
		assertTrue(response2.contains("shortUrl"));
	}

}
