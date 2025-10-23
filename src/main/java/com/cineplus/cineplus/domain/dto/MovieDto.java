package com.cineplus.cineplus.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import com.cineplus.cineplus.domain.entity.MovieStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long id;
    private String title;
    private String synopsis;
    private String genre;
    private String classification;
    private String duration;
    private String cardImageUrl;
    private String bannerUrl;
    private String trailerUrl;
    private List<String> cast;
    private List<String> showtimes;
    private MovieStatus status;
}