package com.pulse.canvas.Dtoes;

import com.pulse.canvas.enums.EventType;
import com.pulse.canvas.enums.UserEventType;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
public class UserLiveEventDTO extends LiveEventDTO {
    private Long userId;
    private UserEventType userEventType;


    public UserLiveEventDTO() {
        this.setEventType(EventType.USER_EVENT);
        this.setTimestamp(System.currentTimeMillis());
    }

    public UserLiveEventDTO(Long userId, Long canvasId, UserEventType userEventType) {
        this.setEventType(EventType.USER_EVENT);
        this.setCanvasId(canvasId);
        this.setTimestamp(System.currentTimeMillis());
        this.userId = userId;
        this.userEventType = userEventType;

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public UserEventType getUserEventType() {
        return userEventType;
    }
    public void setUserEventType(UserEventType userEventType) {
        this.userEventType = userEventType;
    }
}
