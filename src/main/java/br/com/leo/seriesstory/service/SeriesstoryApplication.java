package br.com.leo.seriesstory.service;


import br.com.leo.seriesstory.Controller.Principal;
import br.com.leo.seriesstory.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages ="br.com.leo.seriesstory.repository")
@EntityScan(basePackages = "br.com.leo.seriesstory")

@SpringBootApplication
public class SeriesstoryApplication implements CommandLineRunner {
	@Autowired
	private SerieRepository repositorio;

	public static void main(String[] args) {
		SpringApplication.run(SeriesstoryApplication.class, args);
	}
	@Override
	public void run( String...args) throws Exception{

		Principal principal = new Principal(repositorio);
		principal.mostrarMenu();
	}

}

