package br.com.projeto.screematch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public record DadosEpisodio(
        @JsonAlias ("Season") String temporada,
        @JsonAlias ("Episode") String episodio,
        @JsonAlias ("Title") String tituloEpisodio,
        @JsonAlias ("Plot") String descricao,
        @JsonAlias ("Runtime") String duracao,
        @JsonAlias ("Released") String dataLancamento,
        @JsonAlias ("imdbRating") String avaliacao
) {
}
