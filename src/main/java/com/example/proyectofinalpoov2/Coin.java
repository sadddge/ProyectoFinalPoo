package com.example.proyectofinalpoov2;

import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

import java.util.Objects;

public class Coin extends Entity{

    public Coin(double x, double y) {
        super(x, y, 10);
        Image image = new Image(Objects.requireNonNull
                (getClass().getResourceAsStream("coin.png")));


        getImageView().setImage(image);
        getImageView().setScaleX(1.5);
        getImageView().setScaleY(1.5);
        setImgX(getX()-image.getWidth()/2);
        setImgY(getY()-image.getHeight()/2);
    }

    @Override
    public void detectColision(Shape shape) {
    }

    @Override
    public void handleColision(Shape shape) {

    }
}
