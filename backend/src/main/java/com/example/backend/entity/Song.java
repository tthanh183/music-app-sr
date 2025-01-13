package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;
    String artist;
    String imageUrl;
    String audioUrl;
    int duration;

    @ManyToOne(fetch = FetchType.LAZY)
    Album album;
}
