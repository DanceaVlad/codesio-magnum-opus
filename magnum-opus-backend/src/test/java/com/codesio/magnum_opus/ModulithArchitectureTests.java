package com.codesio.magnum_opus;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithArchitectureTests {

  @Test
  void verifiesApplicationModuleBoundaries() {
    ApplicationModules.of(MagnumOpusApplication.class).verify();
  }

  @Test
  void discoversOnlyExplicitApplicationModules() {
    var modules = ApplicationModules.of(MagnumOpusApplication.class);

    assertThat(modules.stream().map(module -> module.getIdentifier().toString()))
        .containsExactlyInAnyOrder("courts");
  }
}
