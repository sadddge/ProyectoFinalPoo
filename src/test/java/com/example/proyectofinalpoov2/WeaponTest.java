package com.example.proyectofinalpoov2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeaponTest {

    @Test
    void testGetDamage() {
        Entity p1 = new Player(20, 20, 20);
        Weapon w1 = new Weapon(10, p1);
        assertEquals(10, w1.getDamage());
    }
}