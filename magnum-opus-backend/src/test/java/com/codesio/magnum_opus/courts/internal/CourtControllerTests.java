package com.codesio.magnum_opus.courts.internal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CourtControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private CourtRepository courtRepository;

  @BeforeEach
  void deleteCourts() {
    courtRepository.deleteAll();
  }

  @Test
  void createsCourt() throws Exception {
    mockMvc
        .perform(
            post("/api/courts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "Court 1",
                      "type": "INDOOR"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(jsonPath("$.courtId").isNotEmpty())
        .andExpect(jsonPath("$.name").value("Court 1"))
        .andExpect(jsonPath("$.type").value("INDOOR"));
  }

  @Test
  void rejectsInvalidCreateRequest() throws Exception {
    mockMvc
        .perform(
            post("/api/courts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "",
                      "type": null
                    }
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getsAllCourts() throws Exception {
    courtRepository.save(new Court("Court 1", com.codesio.magnum_opus.courts.CourtType.INDOOR));
    courtRepository.save(new Court("Court 2", com.codesio.magnum_opus.courts.CourtType.OUTDOOR));

    mockMvc
        .perform(get("/api/courts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].courtId").isNotEmpty())
        .andExpect(jsonPath("$[0].name").isNotEmpty())
        .andExpect(jsonPath("$[0].type").isNotEmpty());
  }

  @Test
  void allowsLocalFrontendCorsRequests() throws Exception {
    mockMvc
        .perform(
            options("/api/courts")
                .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
        .andExpect(status().isOk())
        .andExpect(
            header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"));
  }
}
