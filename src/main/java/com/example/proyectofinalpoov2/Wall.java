package com.example.proyectofinalpoov2;

import javafx.scene.shape.Shape;

public class Wall extends Obstacle {

    public Wall(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public void detectColision(Shape shape) {
        if (shape.getBoundsInParent().intersects(getBoundsInParent())) {
            handleColision(shape);
        }
    }

    @Override
    public void handleColision(Shape shape) {

    }

    @Override
    public void update() {

    }

    @Override
    public void repaint() {

    }

}
