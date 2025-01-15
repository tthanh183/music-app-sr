package com.example.backend.service;

import com.example.backend.dto.request.SongCreationRequest;

public interface ISongService {
    void createSong(SongCreationRequest songCreationRequest);

}
