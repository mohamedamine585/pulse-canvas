package com.pulse.canvas.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "canvas")
@Data
public class Canvas {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Creator is an Artist, so use ManyToOne for a bidirectional relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Artist creator;


    // One-to-one relation with CanvasPrint
    @OneToOne(mappedBy = "canvas", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private CanvasPrint canvasPrint;

    // Default constructor
    public Canvas() {}

    // Constructor with parameters
    public Canvas(String name, Long creatorId) {
        this.name = name;
    }

    // Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for creator
    public Artist getCreator() {
        return creator;
    }

    public void setCreator(Artist creator) {
        this.creator = creator;
    }



    // Getter and Setter for canvasPrint
    public CanvasPrint getCanvasPrint() {
        return canvasPrint;
    }

    public void setCanvasPrint(CanvasPrint canvasPrint) {
        this.canvasPrint = canvasPrint;
    }
}
