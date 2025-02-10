package com.pulse.canvas.Repositories;

import com.pulse.canvas.entities.CanvasPrint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CanvasPrintRepository extends JpaRepository<CanvasPrint, Long> {
  @Query("SELECT c FROM CanvasPrint c WHERE c.canvas.id = ?1")
    CanvasPrint findByCanvasId(Long canvasId);
}
