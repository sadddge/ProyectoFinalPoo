package com.example.proyectofinalpoov2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EnemyTest {
    @Test
    void testGetTriangles() {
        Enemy e1 = new Enemy(20, 20, 20, 20, 20, 20);
        assertNotNull(e1.getTriangles());
    }
}