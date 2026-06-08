package com.codesio.magnum_opus.courts;

import java.util.UUID;

public record CourtSummary(UUID courtId, String name, CourtType type) {}
