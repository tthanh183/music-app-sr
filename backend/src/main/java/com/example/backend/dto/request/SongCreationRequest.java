package com.example.backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SongCreationRequest {
    String title;
    String artist;
    String imageUrl;
    String audioUrl;
    int duration;
    String albumId;
}
