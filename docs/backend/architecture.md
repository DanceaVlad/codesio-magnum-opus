# Backend Architecture

The backend is a Spring Modulith application on Spring Boot 4 and Java 25.
`MagnumOpusApplication` is annotated with `@Modulithic`, and
`application.yaml` sets `spring.modulith.detection-strategy` to
`explicitly-annotated`.

This document is the backend architecture contract for human contributors and AI
agents.

## Module Model

Each business capability is a Spring Modulith application module. A module is a
direct child package of `com.codesio.magnum_opus` with a `package-info.java`
annotated with `@ApplicationModule`.

Current module:

- `com.codesio.magnum_opus.courts`, displayed as `Courts`

Do not rely on implicit package discovery for new modules. Add the explicit
`@ApplicationModule` annotation in the module package so
`ModulithArchitectureTests` can verify the intended module set.

## Public Module API

The module root package is the only public surface other modules may depend on.
Keep these types in the module root package:

- Public DTO/value records, such as summaries, commands, and query results.
- The module facade interface named `*Management`.
- Published application event records.
- Small public enums that are part of the module contract.

For example, the `courts` module exposes `CourtSummary`, `CourtCreatedEvent`,
`CourtManagement`, and `CourtType` from `com.codesio.magnum_opus.courts`.

Prefer Java records for DTOs and events. They make cross-module data contracts
explicit, immutable, and easy to test.

## Internal Implementation

Implementation details stay under the module's `internal` package.

Place these types in `internal`:

- JPA entities and persistence mappings.
- Spring Data repositories.
- Application services and domain services.
- HTTP controllers and request adapters.
- Event listeners and integration adapters.
- Persistence-specific projections, specifications, mappers, and configuration.

Code outside the module must not import another module's `internal` package.
When a module needs behavior from another module, depend on that module's public
records, `*Management` interface, or published events.

## Cross-Module Interaction

Use direct calls through a public `*Management` interface when the caller needs a
synchronous answer or command result.

Prefer Spring application events when the caller only needs to announce that
something happened. Events keep modules decoupled and make later integration with
asynchronous processing easier.

Do not share repositories, entities, or persistence-specific types between
modules. A module owns its data model and translates it into public records or
events at its boundary.

## Spring Boot And Java Conventions

New backend code follows the repository's Spring Boot 4 and Java 25 defaults:

- Use constructor injection. Do not use field injection.
- Use records for immutable DTOs, commands, query results, and events.
- Use `@ConfigurationProperties` for structured configuration.
- Use Flyway migrations for database schema changes.
- Keep Actuator, Micrometer, health checks, and structured logging in mind for
  production-facing features.
- Prefer Spring Boot managed dependency versions. Pin versions only when the
  dependency is not managed or a compatibility boundary requires it.
- Use Testcontainers for infrastructure integration tests when the behavior
  depends on external systems such as databases, queues, or object stores.

## Enforcement

Architecture enforcement currently comes from:

- `spring.modulith.detection-strategy: explicitly-annotated` in
  `magnum-opus-backend/src/main/resources/application.yaml`.
- `@Modulithic(systemName = "Magnum Opus")` on `MagnumOpusApplication`.
- `ModulithArchitectureTests`, which verifies module boundaries and asserts that
  only explicitly annotated modules are discovered.

Run `mise run check:backend` before merging backend changes.

## Generated Documentation

Do not add generated Spring Modulith diagrams, canvases, or documentation
artifacts for this first architecture pass. The backend is still small, and this
Markdown document is the source of truth for the module authoring rules.

References:

- Spring Modulith reference: https://docs.spring.io/spring-modulith/reference/index.html
- Spring Modulith fundamentals: https://docs.spring.io/spring-modulith/reference/fundamentals.html
- Spring Boot Actuator reference: https://docs.spring.io/spring-boot/4.0/reference/actuator/index.html
- Java SE 25 specification index: https://docs.oracle.com/en/java/javase/25/docs/specs/index.html
