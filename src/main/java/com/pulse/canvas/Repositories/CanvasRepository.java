package com.pulse.canvas.Repositories;

import com.pulse.canvas.entities.Canvas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanvasRepository extends JpaRepository<Canvas, Long> {
}
