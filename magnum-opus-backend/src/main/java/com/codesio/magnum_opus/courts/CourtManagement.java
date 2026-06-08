package com.codesio.magnum_opus.courts;

import java.util.List;

public interface CourtManagement {

  CourtSummary createCourt(CreateCourtCommand command);

  List<CourtSummary> getAllCourts();
}
