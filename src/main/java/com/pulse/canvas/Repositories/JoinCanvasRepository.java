package com.pulse.canvas.Repositories;

import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.JoinCanvas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JoinCanvasRepository extends JpaRepository<JoinCanvas, Long> {


    @Query("delete from JoinCanvas jc where jc.canvas = ?1")
    public void deleteByCanvas(Canvas canvas);
}
