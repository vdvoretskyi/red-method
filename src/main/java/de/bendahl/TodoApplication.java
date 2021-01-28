package de.bendahl;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TodoApplication {

  public static void main(String[] args) {
    SpringApplication.run(TodoApplication.class, args);
  }

  @Bean
  public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() {
    return registry ->
        registry
            .config()
            .meterFilter(
                MeterFilter.deny(
                    id -> {
                      final String uri = id.getTag("uri");
                      return uri == null || !uri.startsWith("/todo");
                    }));
  }

}
