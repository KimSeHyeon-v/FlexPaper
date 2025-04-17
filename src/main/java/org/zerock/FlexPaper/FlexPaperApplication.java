package org.zerock.FlexPaper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FlexPaperApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlexPaperApplication.class, args);
	}

}
