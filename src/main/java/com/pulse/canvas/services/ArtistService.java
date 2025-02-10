package com.pulse.canvas.services;

import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.entities.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    // Create a new artist


    // Get an artist by ID
    public Artist getArtistById(Long id) {
        // Find the artist by ID, throws an exception if not found
        return artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));
    }

    // Get all artists
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }



    // Delete an artist by ID
    @Transactional
    public void deleteArtist(Long id) {
        // Find the artist by ID
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));

        // Delete the artist
        artistRepository.delete(artist);
    }

    // Check if an artist exists by ID
    public boolean existsById(Long id) {
        return artistRepository.existsById(id);
    }
}
