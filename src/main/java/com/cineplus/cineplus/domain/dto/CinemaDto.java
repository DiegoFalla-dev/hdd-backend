package com.cineplus.cineplus.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaDto {
    private Long id;
    private String name;
    private String city;
    private String address;
    private String location;
    private List<String> availableFormats;
    private String image;
}