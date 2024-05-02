package br.com.leo.seriesstory;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages ="br.com.leo.seriesstory.repository")
@EntityScan(basePackages = "br.com.leo.seriesstory")

@SpringBootApplication
public class SeriesstoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeriesstoryApplication.class, args);
	}


}

