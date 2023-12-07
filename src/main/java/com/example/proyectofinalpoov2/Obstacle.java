package com.example.proyectofinalpoov2;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public abstract class Obstacle extends Rectangle implements IUpdate, IColission {

    private final ImageView imageView;

    public Obstacle(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.imageView = new ImageView();
        imageView.setX(x);
        imageView.setY(y);
    }

    public ImageView getImageView() {
        return imageView;
    }

}
