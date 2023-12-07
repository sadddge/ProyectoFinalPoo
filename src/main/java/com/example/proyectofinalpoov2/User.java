package com.example.proyectofinalpoov2;

public class User {
    private final String name;
    private final String password;
    private final int level;

    public User (String name,String password, int level) {
        this.name = name;
        this.password = password;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getLevel() {
        return level;
    }
}
