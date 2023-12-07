package com.example.proyectofinalpoov2;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.util.List;

public abstract class Entity extends Circle implements IUpdate, IColission, IMovable {
    private ImageView imageView;
    private double x;
    private double y;
    private double rad;
    private double velocity;
    private double imgX;
    private double imgY;
    private double hp;
    private boolean canWalkW;
    private boolean canWalkS;
    private boolean canWalkA;
    private boolean canWalkD;
    private boolean dead;
    private Weapon weapon;

    public Entity(double x, double y, double radio) {
        super(x, y, radio);
        this.x = x;
        this.y = y;
        this.rad = radio;
        this.velocity = 5;
        this.imageView = new ImageView();
        hp = 100;
        canWalkW = true;
        canWalkS = true;
        canWalkA = true;
        canWalkD = true;
        dead = false;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRad() {
        return rad;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setRad(double rad) {
        this.rad = rad;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    @Override
    public void moveTo(double x, double y) {
        this.x = x;
        this.y = y;
        imgX = x;
        imgY = y;
    }

    public double getImgX() {
        return imgX;
    }

    public void setImgX(double imgX) {
        this.imgX = imgX;
    }

    public double getImgY() {
        return imgY;
    }

    public void setImgY(double imgY) {
        this.imgY = imgY;
    }

    @Override
    public void moveUp() {
        y -= velocity;
        imgY -= velocity;
    }
    @Override
    public void moveDown() {
        y += velocity;
        imgY += velocity;
    }
    @Override
    public void moveLeft() {
        x -= velocity;
        imgX -= velocity;
    }
    @Override
    public void moveRight() {
        x += velocity;
        imgX += velocity;
    }

    @Override
    public void update() {

    }
    @Override
    public void repaint() {
        if (!dead) {
            Platform.runLater(() -> {
                setCenterX(x);
                setCenterY(y);
                setRadius(rad);
                imageView.setX(imgX);
                imageView.setY(imgY);
                if (weapon != null) {
                    weapon.repaint();
                }
            });
        }
    }

    public void updateCanWalk(List<Obstacle> obstacles) {
        setCanWalkW(true);
        setCanWalkS(true);
        setCanWalkA(true);
        setCanWalkD(true);
        obstacles.forEach(wall -> {
            if (wall instanceof Wall) {
                if (wall.contains(getX(), getY() - getVelocity())) {
                    setCanWalkW(false);
                }
                if (wall.contains(getX(), getY() + getVelocity())) {
                    setCanWalkS(false);
                }
                if (wall.contains(getX() - getVelocity(), getY())) {
                    setCanWalkA(false);
                }
                if (wall.contains(getX() + getVelocity(), getY())) {
                    setCanWalkD(false);
                }
            }
        });
    }

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    public void die() {
        dead = true;
        setRad(0);
        imageView.imageProperty().set(null);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public boolean isCanWalkW() {
        return canWalkW;
    }

    public void setCanWalkW(boolean canWalkW) {
        this.canWalkW = canWalkW;
    }

    public boolean isCanWalkS() {
        return canWalkS;
    }

    public void setCanWalkS(boolean canWalkS) {
        this.canWalkS = canWalkS;
    }

    public boolean isCanWalkA() {
        return canWalkA;
    }

    public void setCanWalkA(boolean canWalkA) {
        this.canWalkA = canWalkA;
    }

    public boolean isCanWalkD() {
        return canWalkD;
    }

    public void setCanWalkD(boolean canWalkD) {
        this.canWalkD = canWalkD;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
