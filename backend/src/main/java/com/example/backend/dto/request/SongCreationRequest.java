package com.example.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SongCreationRequest {
    @NotNull(message = "Title is required")
    @Length(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    String title;
    @NotNull(message = "Artist is required")
    @Length(min = 1, max = 100, message = "Artist must be between 1 and 100 characters")
    String artist;
    String imageUrl;
    @NotNull(message = "Audio URL is required")
    String audioUrl;
    int duration;
    String albumId;
}
