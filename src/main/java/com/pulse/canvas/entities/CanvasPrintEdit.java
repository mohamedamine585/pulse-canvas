package com.pulse.canvas.entities;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;

@Entity(name = "canvasPrintEdit")
public class CanvasPrintEdit {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long canvasPrintEditId;

    private Instant editTime;

    @Lob
    private byte[] edit;

    @ManyToOne()
    private Artist artist;

    @OneToMany(mappedBy = "canvasEdit")
    private List<CanvasPrintEditRange> canvasPrintEditRanges;

    public void setCanvasPrintEditRanges(List<CanvasPrintEditRange> canvasPrintEditRanges) {
        this.canvasPrintEditRanges = canvasPrintEditRanges;
    }

    public void setEdit(byte[] edit) {
        this.edit = edit;
    }

    public byte[] getEdit() {
        return edit;
    }

    public List<CanvasPrintEditRange> getCanvasPrintEditRanges() {
        return canvasPrintEditRanges;
    }

    public void setCanvasPrintEditId(Long canvasPrintEditId) {
        this.canvasPrintEditId = canvasPrintEditId;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Long getCanvasPrintEditId() {
        return canvasPrintEditId;
    }

    public void setEditTime(Instant editTime) {
        this.editTime = editTime;
    }

    public Instant getEditTime() {
        return editTime;
    }

}
