package com.fetch;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(UserTransactionRepository repository) {

		return args -> {
			log.info("Preloading " + repository.save(new Transaction("DANNON", 1000, Instant.parse("2021-11-02T14:00:00Z"))));
			log.info("Preloading " + repository.save(new Transaction("UNILEVER", 200, Instant.parse("2021-10-31T1:00:00Z"))));
			log.info("Preloading " + repository.save(new Transaction("DANNON", -200, Instant.parse("2021-10-31T15:00:00Z"))));
			log.info("Preloading " + repository.save(new Transaction("MILLER COORS", 10000, Instant.parse("2021-11-01T14:00:00Z"))));
			log.info("Preloading " + repository.save(new Transaction("DANNON", 300, Instant.parse("2021-10-31T10:00:00Z"))));
		};
	}
}