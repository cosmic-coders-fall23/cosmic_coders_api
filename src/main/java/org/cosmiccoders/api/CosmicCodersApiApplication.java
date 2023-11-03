package org.cosmiccoders.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CosmicCodersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmicCodersApiApplication.class, args);
    }

}
