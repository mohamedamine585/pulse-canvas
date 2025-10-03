package com.pulse.canvas.Dtoes;

public class ConnectedUserDTO {
    private Long userId;
    private String username;
    private String avatarUrl;
    private Long connectedAt;
    private Long canvasId;
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public Long getConnectedAt() {
        return connectedAt;
    }
    public void setConnectedAt(Long connectedAt) {
        this.connectedAt = connectedAt;
    }

    public Long getCanvasId() {
        return canvasId;
    }

    public void setCanvasId(Long canvasId) {
        this.canvasId = canvasId;
    }
}
