package com.pulse.canvas.Dtoes;

public class CreateUserEvent {
    Long id;
    String email;
    String username;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
