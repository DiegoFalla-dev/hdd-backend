package com.cineplus.cineplus.service;

import com.cineplus.cineplus.web.dto.MovieDTO;
import java.util.List;

public interface MovieService {
    List<MovieDTO> findAll();
    MovieDTO findById(Long id);
}
