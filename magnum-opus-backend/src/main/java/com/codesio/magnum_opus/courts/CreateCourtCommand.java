package com.codesio.magnum_opus.courts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCourtCommand(@NotBlank String name, @NotNull CourtType type) {}
