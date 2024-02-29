package br.com.leo.seriesstory.Controller;

import br.com.leo.seriesstory.model.DadosEpisodio;
import br.com.leo.seriesstory.model.DadosSeries;
import br.com.leo.seriesstory.model.DadosTemporada;
import br.com.leo.seriesstory.model.Episodios;
import br.com.leo.seriesstory.service.ConsumindoApi;
import br.com.leo.seriesstory.service.ConvertendoDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leituraDadosMenu = new Scanner(System.in);
    private ConsumindoApi consumindoApi= new ConsumindoApi();

    private ConvertendoDados conversor = new ConvertendoDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APY_KEY = "&apikey=625848e9";
    public void mostrarMenu() {
        System.out.println("Escolha a Serie de Sua preferencia :");

        var nomeSerie = leituraDadosMenu.nextLine();
        var json = consumindoApi.obtendoDados(
                ENDERECO + nomeSerie.replace(" ", "+") + APY_KEY);

        DadosSeries dados = conversor.obterDados(json, DadosSeries.class);
        System.out.println(dados);


        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int t = 1; t<= dados.totalTemporadas(); t++) {
            json = consumindoApi.obtendoDados
                    (ENDERECO + nomeSerie.replace(" ", "+")
                            + "&season=" + t + APY_KEY);

            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
            temporadas.forEach(System.out::println);
            temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

           List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                   .flatMap(t -> t.episodios()
                           .stream())
                   .collect(Collectors.toList());

        System.out.println("\nTop 5 Episodios: ");
        dadosEpisodios.stream()
                .filter( e -> !e.avaliacao()
                      .equalsIgnoreCase("N/A"))
                .limit(5)
                .forEach(System.out::println);

        System.out.println("#################################################################");
        System.out.println("#################################################################");

        List<Episodios> episodios =  temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                      .map( d -> new Episodios( t.numero(),d)))
                .collect(Collectors.toList());
        episodios.forEach(System.out::println);

        System.out.println("#################################################################");
        System.out.println("#################################################################");

        System.out.println("Para encontra seu Episódio favorito  Digite um Trecho do Titulo !");
          var trechoTitulo = leituraDadosMenu.nextLine();
        Optional<Episodios> episodioBuscado = episodios.stream()
                        .filter(ep -> ep.getTitulo()
                                .toUpperCase()
                                .contains(trechoTitulo.toUpperCase()))
                                .findFirst();
        if (episodioBuscado.isPresent()){
            System.out.println("Episodio encontrado !");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
        }else {
            System.out.println("Episodio não encontrado !");
        }
        System.out.println("#################################################################");
        System.out.println("#################################################################");

        System.out.println("Deseja ver Algum episódio por ano de lancamento ? Digite a data desejada :");
        var ano = leituraDadosMenu.nextInt();
        leituraDadosMenu.nextLine();

        LocalDate buscarPorData = LocalDate.of(ano,1,1);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento()
                        .isAfter(buscarPorData))
                .forEach(e ->{
                    System.out.println(
                            "Temporada: " + e.getTemporada()+
                                    "Episódio: " + e.getTitulo() +
                                    "Data Lancamento: " + e.getDataLancamento().format(formatador)
                    );

                });
        System.out.println("#################################################################");
        System.out.println("#################################################################");


        System.out.println("Notas das avaliações por Temporadas !" );
        Map<Integer,Double> avaliacaoPorTemporadas = episodios.stream()
                .filter(av -> av.getAvaliacao() >0.0)
                .collect(Collectors.groupingBy(Episodios::getTemporada,
                        Collectors.averagingDouble(Episodios::getAvaliacao)));
        System.out.println(avaliacaoPorTemporadas);

        System.out.println("#################################################################");
        System.out.println("#################################################################");

        DoubleSummaryStatistics est = episodios.stream()
                .filter(av -> av.getAvaliacao() >0.0)
                .collect(Collectors.summarizingDouble(Episodios::getAvaliacao));
        System.out.println("Media de Avaliações: " + est.getAverage());
        System.out.println( "Episódios Bem Avaliados: " + est.getMax());
        System.out.println("Episódios Mal Avaliados: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}



