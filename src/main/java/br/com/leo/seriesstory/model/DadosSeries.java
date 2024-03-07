package br.com.leo.seriesstory.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSeries(
                          @JsonAlias("Title") String titulo,
                          @JsonAlias("imdbRating") String avaliacao,
                          @JsonAlias("totalSeasons") Integer totalTemporadas,
                          @JsonAlias("Genre") String genero,
                          @JsonAlias("Actors") String atores,
                          @JsonAlias("Poster") String poster,
                          @JsonAlias("Plot") String sinopse){
}
