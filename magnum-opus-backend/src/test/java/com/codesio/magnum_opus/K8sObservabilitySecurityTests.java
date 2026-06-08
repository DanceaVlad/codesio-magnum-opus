package com.codesio.magnum_opus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("k8s")
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:k8s-observability-security",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.data.redis.host=localhost",
      "management.health.redis.enabled=false",
      "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/magnum-opus"
    })
class K8sObservabilitySecurityTests {

  @Autowired private MockMvc mockMvc;

  @Test
  void permitsHealthProbeSubpathsWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/actuator/health/liveness").contextPath("/api"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }

  @Test
  void keepsPrometheusEndpointAuthenticated() throws Exception {
    mockMvc
        .perform(get("/api/actuator/prometheus").contextPath("/api"))
        .andExpect(status().isUnauthorized());
  }
}
