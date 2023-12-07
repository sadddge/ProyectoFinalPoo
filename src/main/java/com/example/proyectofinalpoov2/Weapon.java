package com.example.proyectofinalpoov2;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Weapon {
    private final double damage;
    private boolean shooting = false;
    private final Entity owner;
    long tiempoUltimoDisparo = 0;
    private final List<Projectile> projectiles;

    public Weapon(double damage, Entity owner) {
        this.damage = damage;
        this.owner = owner;
        projectiles = new ArrayList<>();
    }

    public void update() {
        projectiles.stream().filter(projectile -> projectile.cont < 360).forEach(Projectile::update);
        
    }
    public void repaint() {
        projectiles.stream().filter(projectile -> projectile.cont < 360).forEach(Projectile::repaint);
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public Entity getOwner() {
        return owner;
    }


    public void shot(double targetX, double targetY) {

        long tiempoActual = System.nanoTime();
        double x = owner.getX();
        double y = owner.getY();

        long CADENCE = 300_000_000;
        if (!shooting || tiempoActual - tiempoUltimoDisparo >= CADENCE) {

            double deltaX = targetX - x;
            double deltaY = targetY - y;
            double angle = Math.atan2(deltaY, deltaX);

            Projectile proyectil = new Projectile(x,y,5);
            proyectil.setVelocity(10);

            double velocidadX = Math.cos(angle) * proyectil.getVelocity();
            double velocidadY = Math.sin(angle) * 10;

            proyectil.setSource(this);
            proyectil.setVelocity(velocidadX, velocidadY);

            projectiles.add(proyectil);

            tiempoUltimoDisparo = tiempoActual;
            shooting = true;

            // Inicia un temporizador para permitir disparos nuevamente después de la cadencia
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    shooting = false;
                }
            }, CADENCE / 1_000_000);
        }

    }


    public double getDamage() {
        return damage;
    }
}
