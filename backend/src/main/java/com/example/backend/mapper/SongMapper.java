package com.example.backend.mapper;

import com.example.backend.dto.request.SongCreationRequest;
import com.example.backend.dto.response.SongResponse;
import com.example.backend.entity.Song;
import org.mapstruct.Mapper;

@Mapper
public interface SongMapper {
    Song toSongFromCreat(SongCreationRequest songCreationRequest);
    SongResponse toSongResponse(Song song);
}
