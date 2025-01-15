package com.example.backend.controller;

import com.example.backend.dto.request.SongCreationRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.SongResponse;
import com.example.backend.service.ISongService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SongController {
    ISongService songService;

    @PostMapping()
    public ApiResponse<SongResponse> createSong(@RequestBody SongCreationRequest songCreationRequest) {
        SongResponse songResponse = songService.createSong(songCreationRequest);
        return ApiResponse.<SongResponse>builder()
                .result(songResponse)
                .build();
    }

}
