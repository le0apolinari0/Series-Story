package br.com.leo.seriesstory.Controller;

import br.com.leo.seriesstory.model.DadosSeries;
import br.com.leo.seriesstory.model.DadosTemporada;
import br.com.leo.seriesstory.model.Serie;
import br.com.leo.seriesstory.service.ConsumindoApi;
import br.com.leo.seriesstory.service.ConvertendoDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Principal {
    private Scanner leituraDadosMenu = new Scanner(System.in);
    private ConsumindoApi consumindoApi = new ConsumindoApi();

    private ConvertendoDados conversor = new ConvertendoDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APY_KEY = "&apikey=625848e9";

    private List<DadosSeries> dadosSeries = new ArrayList<>();

    public void mostrarMenu() {
        var opcao = -1;
        while (opcao != 4) {
            var menu = """
                    1 - Buscar Séries !
                    2 - Buscar Por Episódios !
                    3 - Listar Séries Encontradas !
                    4 - Sair da Busca !
                                        
                    """;
            System.out.println(menu);
            opcao = leituraDadosMenu.nextInt();
            leituraDadosMenu.nextLine();
            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesEncontradas();
                    break;
                case 4:
                    System.out.println("Busca finalizada.");
                    break;
                default:
                    System.out.println("Opção digitada e invalida !");
            }
        }

    }

    private void buscarSerieWeb() {
        DadosSeries dados = selecDadosSerie();
        dadosSeries.add(dados);
        System.out.println(dados);
    }

    private DadosSeries selecDadosSerie() {
        System.out.println("Digite o Nome da Serie que Deseja Buscar: ");
        var nomeSerie = leituraDadosMenu.nextLine();
        var json = consumindoApi.obtendoDados(ENDERECO + nomeSerie
                .replace(" ", "+" ) + APY_KEY);

        DadosSeries dados = conversor.obterDados(json, DadosSeries.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        DadosSeries dadosSeries = selecDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int d = 1; d <= dadosSeries.totalTemporadas(); d++) {
            var json = consumindoApi.obtendoDados(ENDERECO + dadosSeries.titulo()
                    .replace(" ", " + " ) + "&season=" + d + APY_KEY);

            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }
    private void listarSeriesEncontradas(){
        List<Serie> series = new ArrayList<>();
        series = dadosSeries.stream()
                .map(da -> new Serie(da))
                .collect(Collectors.toList());
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
}



