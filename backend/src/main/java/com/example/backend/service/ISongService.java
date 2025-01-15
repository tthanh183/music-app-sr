package com.example.backend.service;

import com.example.backend.dto.request.SongCreationRequest;
import com.example.backend.dto.response.SongResponse;

public interface ISongService {
    SongResponse createSong(SongCreationRequest songCreationRequest);
}
