package com.codesio.magnum_opus.courts.internal;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface CourtRepository extends JpaRepository<Court, UUID> {}
