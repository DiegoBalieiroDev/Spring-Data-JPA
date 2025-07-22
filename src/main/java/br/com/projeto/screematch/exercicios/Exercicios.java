package br.com.projeto.screematch.exercicios;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Exercicios {
    public static void main(String[] args) {
        List<String> input = Arrays.asList("10", "abc", "20", "30x");
        List<Integer> numeros = input.stream()
                .map(str -> {
                    try {
                        return Optional.of(Integer.parseInt(str));
                    } catch (NumberFormatException e) {
                        return Optional.<Integer>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        System.out.println(numeros);

        List<String> linhasCsv = List.of(
                "Maria,maria@mail.com",
                "João,joao@mail.com",
                "InvalidoSemMarcador"
        );

        System.out.println("\n");

        List<String> emails = linhasCsv.stream()
                .map(line -> line.split(",")) // separa a string em 2 partes sendo a 1 os nome (maria e joao) e a parte 2 o email
                .filter(parts -> parts.length == 2) // filtra apenas a parte 2 (emails)
                .map(parts -> parts[1]) // escolhe para imprimir a parte
                .toList();
        System.out.println(emails);


        System.out.println("\n");

        System.out.println(processaNumero(Optional.of(5))); // Saída: Optional[25]
        System.out.println(processaNumero(Optional.of(-3))); // Saída: Optional.empty
        System.out.println(processaNumero(Optional.empty())); // Saída: Optional.empty
        System.out.println(processaNumero(Optional.of(8)));


        System.out.println("\n");

        //  Implemente um metodo que recebe uma String representando um nome completo separado por espaços. O metodo deve retornar o primeiro e o último nome após remover os espaços desnecessários.
        System.out.println(obterPrimeiroEUltimoNome("  João Carlos Silva   ")); // Saída: "João Silva"
        System.out.println(obterPrimeiroEUltimoNome("Maria   ")); // Saída: "Maria"


        System.out.println("\n");

        // 4 - Implemente um metodo que verifica se uma frase é um palíndromo. Um palíndromo é uma palavra/frase que, quando lida de trás pra frente, é igual à leitura normal. Um exemplo:

        System.out.println(ehPalindromo("socorram me subi no onibus em marrocos")); // Saída: true
        System.out.println(ehPalindromo("Java")); // Saída: false
        System.out.println(ehPalindromo("Kayak")); // Saída: false


        System.out.println("\n");

        // Implemente um metodo que recebe uma lista de e-mails (String) e retorna uma nova lista onde cada e-mail está convertido para letras minúsculas.
        List<String> emails1 = Arrays.asList("TESTE@EXEMPLO.COM", "exemplo@Java.com ", "Usuario@teste.Com");
        System.out.println(converterEmails(emails1));


        System.out.println("\n");

        System.out.println(Mes.JANEIRO.getNumeroDeDias());
        System.out.println(Mes.ABRIL.getNumeroDeDias());



    }


    // ********************************************************* metodos *********************************************************

    public static List<String> converterEmails(List<String> emails1) {
        return emails1.stream()
                .map(e-> e.toLowerCase())
                .toList();
    }



    public static boolean ehPalindromo(String palavra) {
        String palavaSemEspaco = palavra.replace(" ", "").toLowerCase();
        return new StringBuilder(palavaSemEspaco).reverse().toString().equalsIgnoreCase(palavaSemEspaco);
    }


//     Implemente um metodo que recebe um número inteiro dentro de um Optional. Se o número estiver presente e for positivo, calcule seu quadrado. Caso contrário, retorne Optional.empty.
    public static Optional<Integer> processaNumero(Optional<Integer> numero) {
            return   numero
                    .filter(n-> n > 0)
                    .map(n-> n * n);
    }


    public static String obterPrimeiroEUltimoNome(String nomeCompleto) {
        String[] nomes = nomeCompleto.trim().split("\\s+"); // representa  metacaractere \s que representa qualquer caractere de espaço em branco(espaço, tab, quebra de linha e etc e o + a regex a casar uma ou mais ocorrências
        if (nomes.length == 1) {
            return nomes[0];
        }
        return nomes[0] + " " + nomes[nomes.length - 1];

    }
}
