package com.example.proyectofinalpoov2;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class GameControllerTest {




    @Test
    void testGetMazeActual() {
        GameController g1 = new GameController();
        assertNotNull(g1.getMazeActual());
    }


}