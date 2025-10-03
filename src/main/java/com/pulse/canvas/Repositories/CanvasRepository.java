package com.pulse.canvas.Repositories;

import com.pulse.canvas.entities.Canvas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanvasRepository extends JpaRepository<Canvas, Long> {

    public List<Canvas> findByCreatorId(Long creatorId);
}
