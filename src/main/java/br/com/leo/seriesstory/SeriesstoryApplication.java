package br.com.leo.seriesstory;

import br.com.leo.seriesstory.model.DadosEpisodio;
import br.com.leo.seriesstory.model.DadosSeries;
import br.com.leo.seriesstory.service.ConsumindoApi;
import br.com.leo.seriesstory.service.ConvertendoDados;
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
		var consumoApi = new ConsumindoApi();
		var json = consumoApi.obtendoDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=625848e9");
		System.out.println(json);

		json = consumoApi.obtendoDados("https://coffee.alexflipnote.dev/random.json");
		System.out.println(json);

		ConvertendoDados conversor = new ConvertendoDados();
		DadosSeries dados = conversor.obterDados(json,DadosSeries.class);
		System.out.println(dados);

		json = consumoApi.obtendoDados("https://omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=625848e9");
		DadosEpisodio dadosEpisodio = conversor.obterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);



	}

}
