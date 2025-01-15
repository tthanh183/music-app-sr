package com.example.backend.repository;

import com.example.backend.dto.response.SongCreationResponse;
import com.example.backend.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<String, Song> {

}
