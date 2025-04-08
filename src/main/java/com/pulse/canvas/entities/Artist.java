package com.pulse.canvas.entities;

import jakarta.persistence.*;

import java.util.List;


@Entity
public class Artist {

    @Id
    private Long id;
    private String username;
    private String bio;
    String email;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @OneToMany(mappedBy = "artist")
    private List<CanvasPrintEdit> canvasPrintEdits;

    public void setCanvasPrintEdits(List<CanvasPrintEdit> canvasPrintEdits) {
        this.canvasPrintEdits = canvasPrintEdits;
    }

    public List<CanvasPrintEdit> getCanvasPrintEdits() {
        return canvasPrintEdits;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}