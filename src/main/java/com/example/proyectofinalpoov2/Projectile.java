package com.example.proyectofinalpoov2;

import javafx.scene.shape.Shape;

public class Projectile extends Entity {

    private Weapon source;
    private double velocityX;
    private double velocityY;
    int cont = 0;

    public Projectile(double x, double y, double radio) {
        super(x, y, radio);
        setHp(1);
    }

    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    @Override
    public void update() {
        cont++;
        double newX = getX() + velocityX;
        double newY = getY() + velocityY;
        setX(newX);
        setY(newY);
    }
    @Override
    public void detectColision(Shape shape) {
        if (shape.getBoundsInParent().intersects(getBoundsInParent())) {
            handleColision(shape);
        }
    }

    @Override
    public void handleColision(Shape shape) {
        if (shape instanceof Wall) {
            setRad(0);
        } else if (shape instanceof Enemy enemy) {
            enemy.setHp(getHp()-source.getDamage());
        }
    }

    public Weapon getSource() {
        return source;
    }

    public void setSource(Weapon source) {
        this.source = source;
    }
}
