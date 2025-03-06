package com.pulse.canvas.Repositories;

import com.pulse.canvas.entities.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {


        // Native SQL Insert Query



    @Query("select a from Artist  a where a.username = ?1")
   public Artist findByUsername(String username);

    @Query(value = "INSERT INTO artist (username, bio) VALUES (:username, :bio)", nativeQuery = true)
    public void insertArtist(@Param("username") String username, @Param("bio") String bio);


}
