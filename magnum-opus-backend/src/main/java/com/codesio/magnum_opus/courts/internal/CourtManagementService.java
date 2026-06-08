package com.codesio.magnum_opus.courts.internal;

import com.codesio.magnum_opus.courts.CourtCreatedEvent;
import com.codesio.magnum_opus.courts.CourtManagement;
import com.codesio.magnum_opus.courts.CourtSummary;
import com.codesio.magnum_opus.courts.CreateCourtCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class CourtManagementService implements CourtManagement {

  private final CourtRepository courtRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public CourtSummary createCourt(CreateCourtCommand command) {
    final Court court = courtRepository.save(new Court(command.name(), command.type()));
    eventPublisher.publishEvent(new CourtCreatedEvent(court.getId()));

    return toSummary(court);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CourtSummary> getAllCourts() {
    return courtRepository.findAll().stream().map(CourtManagementService::toSummary).toList();
  }

  private static CourtSummary toSummary(Court court) {
    return new CourtSummary(court.getId(), court.getName(), court.getType());
  }
}
