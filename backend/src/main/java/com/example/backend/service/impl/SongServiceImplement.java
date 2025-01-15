package com.example.backend.service.impl;

import com.example.backend.dto.request.SongCreationRequest;
import com.example.backend.dto.response.SongResponse;
import com.example.backend.entity.Song;
import com.example.backend.mapper.SongMapper;
import com.example.backend.repository.SongRepository;
import com.example.backend.service.ISongService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SongServiceImplement implements ISongService {
    SongRepository songRepository;
    SongMapper songMapper;

    @Override
    public SongResponse createSong(SongCreationRequest songCreationRequest) {
        Song song = songMapper.toSongFromCreat(songCreationRequest);
        songRepository.save(song);
        return songMapper.toSongResponse(song);
    }
}
