package kr.modusplant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ModusplantApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModusplantApplication.class, args);
	}

}
