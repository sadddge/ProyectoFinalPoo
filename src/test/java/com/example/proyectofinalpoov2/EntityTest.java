package com.example.proyectofinalpoov2;

import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {



    @Test
    void testMoveTo() {
        Entity e1 = new TestEntity(20, 20, 20);
        e1.moveTo(30, 30);
        assertEquals(30, e1.getX());
        assertEquals(30, e1.getY());
    }



    @Test
    void testUpdateCanWalk() {
        Entity e1 = new TestEntity(20, 20, 20);
        List<Obstacle> obstacleList = List.of(new Wall(20, 20, 20, 20));
        e1.updateCanWalk(obstacleList);
        assertTrue(e1.isCanWalkW());
        assertTrue(e1.isCanWalkS());
        assertTrue(e1.isCanWalkA());
        assertTrue(e1.isCanWalkD());
    }

    @Test
    void testDie() {
        Entity e1 = new TestEntity(20, 20, 20);
        e1.die();
        assertTrue(e1.isDead());
        assertEquals(0, e1.getRad());
        assertNull(e1.getImageView().getImage());
    }


    @Test
    void testSetWeapon() {
        Entity e1 = new TestEntity(200, 20, 20);
        Weapon w1 = new Weapon(20, e1);
        e1.setWeapon(w1);
        assertEquals(w1, e1.getWeapon());
    }

    private static class TestEntity extends Entity {
        public TestEntity(double x, double y, double radio) {
            super(x, y, radio);
        }

        @Override
        public void detectColision(Shape shape) {

        }

        @Override
        public void handleColision(Shape shape) {

        }
    }
}