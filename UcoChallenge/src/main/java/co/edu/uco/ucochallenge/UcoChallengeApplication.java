package co.edu.uco.ucochallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UcoChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(UcoChallengeApplication.class, args);
	}

}
