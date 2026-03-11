# Desafio Técnico Itaú - API de Encurtamento de URL 🔗

Esta é uma API RESTful desenvolvida para encurtar URLs (estilo bit.ly), redirecionar acessos e contabilizar cliques. O projeto foi construído focando em simplicidade, Clean Code e princípios SOLID, evitando overengineering, conforme solicitado no desafio.

## 🛠️ Stack Tecnológica

* **Linguagem:** Java 
* **Framework:** Spring Boot 3
* **Camada Web:** Spring Web, Spring Boot Validation
* **Persistência:** Spring Data JPA
* **Banco de Dados:** H2 (In-Memory) para facilitar a execução local e testes sem dependências externas
* **Testes:** JUnit 5, MockMvc e Spring Boot Test.
* **Outros:** Apache Commons Lang (para geração de strings seguras), Docker

## 🏗️ Decisões de Arquitetura

1.  **Geração do ID Curto e Colisões:**
    * **Estratégia:** Foi adotada a geração de uma string alfanumérica aleatória (Base62) com 6 caracteres via `RandomStringUtils`.
    * **Proteção contra Colisões:** O Service possui um loop de verificação simples (`while`) que valida a existência do ID no banco. O banco de dados atua como a última linha de defesa com a anotação `@Column(unique = true)` no identificador curto, garantindo a integridade dos dados mesmo em cenários de alta concorrência.
2.  **Redirecionamento (HTTP 302):**
    * Para o redirecionamento, foi escolhido o status HTTP **302 (Found)** em vez do 301. Isso garante que o navegador não faça um cache permanente do redirecionamento, forçando com que todas as requisições passem pela nossa API, permitindo a **contabilização precisa dos cliques (`clickCount`)**.
3.  **Separação de Responsabilidades:**
    * Foram criados dois controllers distintos: `UrlController` para gerenciamento (`/v1/urls`) e `RedirectController` focado apenas na resolução das URLs (`/r/{shortId}`).
4.  **Tratamento de Erros Global:**
    * Foi implementado um `@RestControllerAdvice` (`GlobalExceptionHandler`) para interceptar exceções (como erro de validação do `@Valid` e `ResourceNotFoundException`), padronizando o JSON de resposta de erro conforme exigido.

## 🚀 Como Executar o Projeto

**Pré-requisitos:** Java 17+ e Maven instalados.

1.  Clone o repositório.
2.  Abra o terminal na pasta raiz do projeto.
3.  Execute o comando do Maven wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```
    *(No Windows, use `mvnw spring-boot:run`)*
4.  A aplicação iniciará na porta `8081`.

### Opção 2: Via Docker (Recomendado)
**Pré-requisitos:** Docker e Docker Compose instalados.

1. Clone o repositório e abra o terminal na raiz do projeto.
2. Execute o comando para construir e subir o container:
   ```bash
   docker-compose up --build

## 🧪 Como Rodar os Testes

O projeto conta com testes de integração que cobrem o fluxo de ponta a ponta usando o banco de dados H2.

Para executar a suíte de testes, rode no terminal:
```bash
./mvnw test