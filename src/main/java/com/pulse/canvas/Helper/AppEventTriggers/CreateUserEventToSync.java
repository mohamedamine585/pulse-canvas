package com.pulse.canvas.Helper.AppEventTriggers;

import com.pulse.canvas.Dtoes.CreateUserEvent;
import org.springframework.context.ApplicationEvent;

public class CreateUserEventToSync extends ApplicationEvent {
    private CreateUserEvent createUserEvent;

    public CreateUserEventToSync(Object source) {
        super(source);
    }

    public CreateUserEvent getCreateUserEvent() {
        return createUserEvent;
    }

    public void setCreateUserEvent(CreateUserEvent createUserEvent) {
        this.createUserEvent = createUserEvent;
    }
}

