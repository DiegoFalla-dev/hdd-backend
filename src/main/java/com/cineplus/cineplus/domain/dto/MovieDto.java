package com.cineplus.cineplus.domain.dto;

import com.cineplus.cineplus.domain.entity.MovieStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

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
    private String cardImage;    // 414x621px or 600x900px image URL
    private String banner;       // 1280x480px or 1920x720px image URL
    private String trailerUrl;
    private List<String> cast;
    private List<String> showtimes;
    private MovieStatus status;
}