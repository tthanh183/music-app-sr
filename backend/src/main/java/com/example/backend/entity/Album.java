package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;
    String artist;
    String imageUrl;
    int releaseYear;
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    List<Song> songs;
}
