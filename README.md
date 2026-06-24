# Code Review Bot

An AI-powered code review tool built with Spring Boot that hunts for production failure modes — race conditions, null safety issues, security vulnerabilities, resource leaks — instead of style nitpicks.

## Tech Stack

- **Spring Boot 3.4** — REST API, dependency injection, auto-configuration
- **Spring Data JPA** — Database persistence with H2
- **Spring AI** — LLM integration via Groq (Llama 3.3 70B)
- **Lombok** — Boilerplate reduction
- **Maven** — Build and dependency management
- **Java 21**

## How It Works

1. Code is submitted via REST endpoint
2. An LLM analyzes it as a "paranoid senior engineer" — looking only for production failure modes
3. Findings are returned as structured JSON with category, severity, failure scenario, and fix suggestion
4. All reviews are persisted for later retrieval and analytics

## Running Locally

```bash
export GROQ_API_KEY=your_groq_key_here
./mvnw spring-boot:run
```

## Example

```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Content-Type: application/json" \
  -d '{"code": "your code here", "language": "java", "filename": "Example.java"}'
```

## Status

🚧 Work in progress — building toward GitHub App integration and live demo deployment.