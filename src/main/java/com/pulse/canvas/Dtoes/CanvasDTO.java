package com.pulse.canvas.Dtoes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CanvasDTO {
    private String canvasName;
    private Long id;
    private Long creatorId;

    private Boolean isPrivate;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getCreatorId() {
        return creatorId;
    }


    public void setCanvasName(String canvasName) {
        this.canvasName = canvasName;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public String getCanvasName() {
        return canvasName;
    }
}
