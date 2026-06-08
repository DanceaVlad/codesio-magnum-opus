package com.codesio.magnum_opus.courts.internal;

import com.codesio.magnum_opus.courts.CourtManagement;
import com.codesio.magnum_opus.courts.CourtSummary;
import com.codesio.magnum_opus.courts.CreateCourtCommand;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
class CourtController {

  private final CourtManagement courtManagement;

  @GetMapping
  List<CourtSummary> getAllCourts() {
    return courtManagement.getAllCourts();
  }

  @PostMapping
  ResponseEntity<CourtSummary> createCourt(@Valid @RequestBody CreateCourtCommand command) {
    final CourtSummary court = courtManagement.createCourt(command);

    return ResponseEntity.created(URI.create("/api/courts/" + court.courtId())).body(court);
  }
}
