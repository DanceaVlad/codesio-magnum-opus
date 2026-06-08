package com.codesio.magnum_opus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@Modulithic(systemName = "Magnum Opus")
@SpringBootApplication
public class MagnumOpusApplication {

  public static void main(String[] args) {
    SpringApplication.run(MagnumOpusApplication.class, args);
  }
}
