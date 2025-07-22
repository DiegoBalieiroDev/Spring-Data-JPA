package br.com.projeto.screematch.principal;

import br.com.projeto.screematch.model.*;
import br.com.projeto.screematch.repository.SerieRepository;
import br.com.projeto.screematch.service.ConsumoAPI;
import br.com.projeto.screematch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner scan = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = System.getenv("OMDB_APIKEY");
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }


    // "https://www.omdbapi.com/?t=supernatural&Season=" + i + "&apikey=44e7b972"


    public void exibeMenuPrincipal() {
        int opcao;
        do {
            System.out.println("""
                    \n\n*** Escolha uma das opções abaixo ***
                    1 - Informações básicas série
                    2 - Informações de temporada e episódio
                    3 - Busca Top 10 da série
                    4 - Estatísticas da série
                    5 - Episódios mais detalhados
                    6 - Listar séries Banco de Dados
                    7 - Listar séries buscadas na sessão
                    0 - Sair
                    """);
            while (!scan.hasNextInt()) {
                System.out.println("Digite um número válido.");
                scan.next();
            }
            opcao = scan.nextInt();
            scan.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    BuscarEpisodioPorSerie();
                    break;
                case 3:
                    BuscaTop10();
                    break;
                case 4:
                    estatisticasSerie();
                    break;
                case 5:
                    exibeEpisodiosDetalhadosDaTemporada();
                    break;
                case 6:
                    listarSeriesBD();
                    break;
                case 7:
                    listarSeriesBuscadas();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Essa opção não existe. Tente novamente");
            }
        } while (opcao != 0);
        System.out.println("Encerrando o sistema...");
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.print("Digite o nome da série para busca:");
        var nomeSerie = scan.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void BuscarEpisodioPorSerie() {
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        //temporadas.forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.temporada(), e)))
                .collect(Collectors.toList());
        System.out.println("\n\n");
        episodios.forEach(System.out::println);
    }

    private void BuscaTop10() {
        DadosSerie dadosTop10 = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosTop10.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosTop10.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        List<DadosEpisodioTemporada> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro(N/A) " + e))
                .sorted(Comparator.comparing(DadosEpisodioTemporada::avaliacao).reversed()) // vai organizar fazendo com que percorra todas as avaliações de DadosEpisodiosTemporada de forma decrescente por causa do reversed
//                .peek(e -> System.out.println("Ordenação: " + e) )
                .limit(10)
//                .peek(e -> System.out.println("Limite: " + e))
                .map(e -> "Episodio " + e.numeroEpisodio() + " " + e.tituloEpisodio().toUpperCase() + " - " + e.avaliacao())
//                .peek(e -> System.out.println("Mapeamento " + e))

                .forEach(System.out::println);

    }


    private void estatisticasSerie() {

        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.temporada(), e)))
                .collect(Collectors.toList());

        System.out.println("Avaliação temporadas: ");
        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        // fim "Imprime os dados da série com temporada + episódios detalhados"

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage() +
                "\nNota máxima: " + est.getMax() +
                "\nNota mínima: " + est.getMin());
    }


    private void listarSeriesBuscadas() {
        List<Serie> series = new ArrayList<>();
       series =  dadosSeries.stream()
                        .map(d-> new Serie(d))
                                .collect(Collectors.toList());

       series.stream()
               .sorted(Comparator.comparing(Serie::getGenero))
               .forEach(System.out::println);
    }


    private void listarSeriesBD(){
        List<Serie> series = new ArrayList<>();
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }


    public void exibeEpisodiosDetalhadosDaTemporada() {


        List<DadosEpisodio> episodiosDetalhados = new ArrayList<>();
        System.out.print("Serie:");
        var nomeSerie = scan.nextLine();
        System.out.print("Temporada:");
        int temporada = scan.nextInt();

        String jsonTemporada = consumo.obterDados("https://www.omdbapi.com/?t=" + nomeSerie +"&Season=" + temporada + "&apikey=44e7b972");
        DadosTemporada dadosTemporada = conversor.obterDados(jsonTemporada, DadosTemporada.class);
        int totalEpisodios = dadosTemporada.episodios().size();

        for (int i = 1; i <= totalEpisodios; i++) {
            //"https://www.omdbapi.com/?t=supernatural&Season="+ temporada +"&Episode=" + i + "&apikey=44e7b972"
            String jsonEpisodio = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + temporada + "&episode="+ i + API_KEY);
            DadosEpisodio episodioDetalhado = conversor.obterDados(jsonEpisodio, DadosEpisodio.class);
            episodiosDetalhados.add(episodioDetalhado);
            episodiosDetalhados.forEach(System.out::println);
        }
    }

}

