package com.pulse.canvas.services;

import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.Repositories.CanvasPrintEditRepository;
import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.CanvasPrintEdit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PrintEditService
{
    @Autowired
    CanvasPrintEditRepository canvasPrintEditRepository;

    @Autowired
    ArtistRepository artistRepository;

    public CanvasPrintEdit saveCanvasPrintEdit(Long artistId , byte[] pixels , Instant editTime){
        try {
            Artist artist = artistRepository.getById(artistId);
            if(artist == null)
                throw new Exception("Artist not found");
            CanvasPrintEdit canvasPrintEdit = new CanvasPrintEdit();
            canvasPrintEdit.setArtist(artist);
            canvasPrintEdit.setEdit(pixels);
            canvasPrintEdit.setEditTime(editTime);
           return canvasPrintEditRepository.save(canvasPrintEdit);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
