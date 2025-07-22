package br.com.projeto.screematch;

import br.com.projeto.screematch.principal.Principal;
import br.com.projeto.screematch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Scanner;

@SpringBootApplication
public class ScreematchApplication implements CommandLineRunner {

	@Autowired
	private SerieRepository repositorio;

	public static void main(String[] args) {
		SpringApplication.run(ScreematchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner scan = new Scanner(System.in);
		Principal principal = new Principal(repositorio);


		principal.exibeMenuPrincipal();


	}
}
