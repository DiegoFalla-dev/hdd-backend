package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.List;

@Entity
@Table(name = "cinemas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String city;

    @Column(length = 255)
    private String address;

    @Column(length = 255)
    private String location;

    @ElementCollection
    @CollectionTable(name = "cinema_available_formats", joinColumns = @JoinColumn(name = "cinema_id"))
    @Column(name = "format")
    private List<String> availableFormats;

    // Optional image field (can be URL or base64). Not indispensable.
    private String image;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Theater> theaters;
}