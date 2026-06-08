package com.codesio.magnum_opus.courts.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.codesio.magnum_opus.courts.CourtManagement;
import com.codesio.magnum_opus.courts.CourtType;
import com.codesio.magnum_opus.courts.CreateCourtCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CourtManagementServiceTests {

  @Autowired private CourtManagement courtManagement;

  @Autowired private CourtRepository courtRepository;

  @BeforeEach
  void deleteCourts() {
    courtRepository.deleteAll();
  }

  @Test
  void createsCourt() {
    var court =
        courtManagement.createCourt(new CreateCourtCommand("Centre Court", CourtType.INDOOR));

    assertThat(court.courtId()).isNotNull();
    assertThat(court.name()).isEqualTo("Centre Court");
    assertThat(court.type()).isEqualTo(CourtType.INDOOR);
    assertThat(courtRepository.findById(court.courtId())).isPresent();
  }

  @Test
  void getsAllCourts() {
    var indoor = courtManagement.createCourt(new CreateCourtCommand("Indoor 1", CourtType.INDOOR));
    var outdoor =
        courtManagement.createCourt(new CreateCourtCommand("Outdoor 1", CourtType.OUTDOOR));

    assertThat(courtManagement.getAllCourts()).containsExactlyInAnyOrder(indoor, outdoor);
  }
}
