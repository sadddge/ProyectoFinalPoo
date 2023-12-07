package com.example.proyectofinalpoov2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PlayerTest {




    @Test
    void testSetAndGetCoinCont() {
        Player p1 = new Player(10, 20, 10);
        p1.setCoinCont(5);
        assertEquals(5, p1.getCoinCont());
    }

    @Test
    void testSetAndGetLevel() {
        Player p1 = new Player(10, 20, 10);
        p1.setLevel(2);
        assertSame(2, p1.getLevel());
    }


    @BeforeEach
    void setUp(){

        Player p1= new Player(80,70,20);

    }

}