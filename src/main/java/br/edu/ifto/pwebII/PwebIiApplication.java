package br.edu.ifto.pwebII;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PwebIiApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(PwebIiApplication.class, args);

		PasswordEncoder passwordEncoder =
				context.getBean(PasswordEncoder.class);

		String hash = passwordEncoder.encode("123");

//		System.out.println("HASH PARA TESTE: " + hash);

	}
}
