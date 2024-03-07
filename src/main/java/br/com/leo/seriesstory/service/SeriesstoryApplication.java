package br.com.leo.seriesstory.service;

import br.com.leo.seriesstory.Controller.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
public class SeriesstoryApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SeriesstoryApplication.class, args);
	}
	@Override
	public void run( String...args) throws Exception{

		Principal principal = new Principal();
		principal.mostrarMenu();
	}

}

