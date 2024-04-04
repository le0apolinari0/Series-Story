package br.com.leo.seriesstory.Controller;

import br.com.leo.seriesstory.model.*;
import br.com.leo.seriesstory.repository.SerieRepository;
import br.com.leo.seriesstory.service.ConsumindoApi;
import br.com.leo.seriesstory.service.ConvertendoDados;


import java.util.*;
import java.util.stream.Collectors;


public class Principal {
    private Scanner leituraDadosMenu = new Scanner(System.in);
    private ConsumindoApi consumindoApi = new ConsumindoApi();

    private ConvertendoDados conversor = new ConvertendoDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APY_KEY = "&apikey=625848e9";

    private List<DadosSeries> dadosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBuscar;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void mostrarMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar Séries !
                    2 - Buscar Por Episódios !
                    3 - Listar Séries Encontradas !
                    4 - Buscar Séries por nome !
                    5 - Buscara Séries por Ator !
                    6 - Buscar as Top 5 Séries !
                    7 - Buscar Séries por Categoria !
                    8 - Filtrar Séries !
                    9 - Filtrar Episodios por Trecho !
                    10 - Buscar as Top 5 Episodios !
                    11 - Buscar episódios a partir de uma data
                    ***********************************
                    0 - Sair da Busca !
                                        
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
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtrarSeries();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10: buscarTop5EpisodioPorSerie();
                break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Busca finalizada.");
                    break;
                default:
                    System.out.println("Opção digitada e invalida !");
            }
        }

    }




    private void buscarSerieWeb() {
        DadosSeries dados = selecDadosSerie();
        Serie serie = new Serie(dados);
        // dadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSeries selecDadosSerie() {
        System.out.println("Digite o Nome da Serie que Deseja Buscar: ");
        var nomeSerie = leituraDadosMenu.nextLine();
        var json = consumindoApi.obtendoDados(ENDERECO + nomeSerie
                .replace(" ", "+") + APY_KEY);

        DadosSeries dados = conversor.obterDados(json, DadosSeries.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesEncontradas();
        System.out.println("Escolha uma série pelo nome !");
        var nomeDaSerie = leituraDadosMenu.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeDaSerie);
        if (serie.isPresent()) {
            var seriesEncontradas = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int d = 1; d <= seriesEncontradas.getTotalTemporadas(); d++) {
                var json = consumindoApi.obtendoDados(ENDERECO + seriesEncontradas.getTitulo()
                        .replace(" ", " + ") + "&season=" + d + APY_KEY);

                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodios> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodios(d.numero(), e)))
                    .collect(Collectors.toList());
            seriesEncontradas.setEpisodios(episodios);
            repositorio.save(seriesEncontradas);

        } else {
            System.out.println("Série não encontrada ");
        }
    }

    private void listarSeriesEncontradas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma Série por um Titulo !");
        var nomeSerie = leituraDadosMenu.nextLine();
         serieBuscar = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscar.isPresent()){
            System.out.println(" Dados da Série Encontrada: " + serieBuscar.get());
        } else {
            System.out.println("Série não encontrada.");
        }
    }
    private void buscarSeriesPorAtor() {
        System.out.println("Qual o nome do Ator ? ");
        var nomeAtor= leituraDadosMenu.nextLine();

        System.out.println("Avaliações apartir de qual valor ?");
        var avaliacao = leituraDadosMenu.nextDouble();
        List<Serie> seiresEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual
                (nomeAtor, avaliacao);

        System.out.println("Series em que " + nomeAtor + " Trabalhou ");
        seiresEncontradas.forEach( s ->
                System.out.println(s.getTitulo() + " Avaliação : " + s.getAvaliacao()));
    }
    private void buscarTop5Series() {
    List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
    seriesTop.forEach( s ->
            System.out.println(s.getTitulo() + " Avaliação : " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria(){
        System.out.println("Deseja Buscar serie de qual categoria ? ");
        var nomeGenero = leituraDadosMenu.nextLine();

        Categoria categoria = Categoria.fromPortugues(nomeGenero);
    List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println(" Séries da categoria " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }
    private void filtrarSeries() {
        System.out.println("Quantas Temporada de Séries deseja filtrar ?");
        var totalTemporadas = leituraDadosMenu.nextInt();
        leituraDadosMenu.nextLine();

        System.out.println("Deseja Filtrar a partir de qual valor de Avaliação ?");
        var avaliacao = leituraDadosMenu.nextDouble();
        leituraDadosMenu.nextLine();

        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("Séries filtradas ");
        filtroSeries.forEach( s -> System.out.println(s.getTitulo() + " - Avaliação : " + s.getAvaliacao()));

    }
    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite um Trecho do Episodio que deseja Buscar ! ");
                var trechoEpisodio= leituraDadosMenu.nextLine();
        List<Episodios> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(System.out::println);
    }
    private void buscarTop5EpisodioPorSerie() {
        buscarSeriePorTitulo();
        if (serieBuscar.isPresent()) {
            Serie serie = serieBuscar.get();
          List<Episodios> episodioTop5 = repositorio.topEpisodioPorSerie(serie);
            episodioTop5.forEach(System.out::println);
        }
    }
    private void buscarEpisodiosDepoisDeUmaData() {
        buscarSeriePorTitulo();
        if (serieBuscar.isPresent()){
            Serie serie = serieBuscar.get();
            System.out.println("Digite o ano limite de lançamento desejado !");
            var anoLancamento = leituraDadosMenu.nextInt();
            leituraDadosMenu.nextLine();

            List<Episodios> episodiosAnoLancamento = repositorio.episodioPorSerieEAno( serie, anoLancamento);
            episodiosAnoLancamento.forEach(System.out::println);
        }

    }
}

//Diferenciando os tipos de consulta da JPA

