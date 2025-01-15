package com.example.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SongResponse {
    String id;
    String title;
    String artist;
    String imageUrl;
    String audioUrl;
    int duration;
    String albumId;
}
