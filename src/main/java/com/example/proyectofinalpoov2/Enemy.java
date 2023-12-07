package com.example.proyectofinalpoov2;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.*;
import java.util.stream.IntStream;

public class Enemy extends Entity {

    private double targetX;
    private double targetY;
    private final double startX;
    private final double startY;
    private final double endX;
    private final double endY;
    private double velocityX;
    private double velocityY;
    private boolean isPlatrlling;
    private boolean playerInSight;
    private boolean canWalk;
    private List<Point2D> points;
    private List<Polygon> triangles;
    public Enemy(double x, double y, double radio, double damage, double endX, double endY) {
        super(x, y, radio);
        this.startX = x;
        this.startY = y;
        this.endX = endX;
        this.endY = endY;
        targetX = endX;
        targetY = endY;
        isPlatrlling = true;
        playerInSight = false;
        canWalk = true;
        points = new ArrayList<>();
        triangles = new ArrayList<>();

        setVelocity(2);


        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("Enemigo1.png")));
        getImageView().setImage(image);
        setImgX(getX()-image.getWidth()/2);
        setImgY(getY()-image.getHeight()/2);
        setWeapon(new Weapon(damage, this));
    }

    //Update
    @Override
    public void update() {
        if (!isDead()) {
            if (getHp() <= 0) {
                die();
            }

            if (canWalk) {
                double newX = getX() + velocityX;
                double newY = getY() + velocityY;

                setX(newX);
                setY(newY);
            }

            if (getImageView().getImage() != null) {
                setImgX(getX()-getImageView().getImage().getWidth()/2);
                setImgY(getY()-getImageView().getImage().getHeight()/2);
            }
            if (isPlatrlling) {
                patrol();
            }
            getWeapon().update();
        }
    }

    public void updateRays(List<Line> lines) {
        if (!isDead()) {
            calRays(lines,getX(),getY());
            mergeSort(points); //Ordena los puntos por angulo con respecto al eje X
            triangles = updateTriangles();
        } else triangles = new ArrayList<>();
    }


    //Movement
    public void setVelocityToTarget() {
        double deltaX = targetX - getX();
        double deltaY = targetY - getY();
        double angle = Math.atan2(deltaY, deltaX);

        velocityX = Math.cos(angle) * getVelocity();
        velocityY = Math.sin(angle) * getVelocity();
    }

    private boolean isClose(double x, double y, double dist) {
        return getX() <= x + dist && getX() >= x - dist && getY() >= y - dist && getY() <= y + dist;
    }

    private void patrol() {
        if (startX == endX && startY == endY) {
            stop();
        } else {
            if (isClose(startX, startY, 5)) {
                targetX = endX;
                targetY = endY;
            } else if (isClose(endX, endY, 5)) {
                targetX = startX;
                targetY = startY;
            }
            setVelocityToTarget();
        }
    }

    private void chase(Entity entity) {
        isPlatrlling = false;
        targetX = entity.getX();
        targetY = entity.getY();
        if (isClose(entity.getX(),entity.getY(),150)){
            stop();
        } else {
            setVelocityToTarget();
        }
    }

    private void stop() {
        velocityX = 0;
        velocityY = 0;
    }

    private void shot() {
        double minX = targetX - 15;
        double maxX = targetX + 15;
        double minY = targetY - 15;
        double maxY = targetY + 15;
        double rangeX = maxX - minX + 1;
        double rangeY = maxY - minY + 1;

        double aimX = ((Math.random() * rangeX) + minX);
        double aimY = ((Math.random() * rangeY) + minY);
        getWeapon().shot(aimX,aimY);
    }
    //Colisions
    @Override
    public void detectColision(Shape shape) {
        if (!isDead()) {
            if (shape.contains(getX(),getY())) {
                handleColision(shape);
            }
        }
    }
    @Override
    public void handleColision(Shape shape) {
        if (shape instanceof Wall){
            if (shape.contains(nextPoint())) {
                canWalk = false;
            }
        }else if (shape instanceof Projectile projectile) {
            if (!projectile.getSource().getOwner().equals(this)) {
                setHp(getHp()-(projectile.getSource().getDamage()));
                projectile.setVelocity(0,0);
                projectile.moveTo(2000,2000);
                System.out.println(getHp());
            }
        }

    }
    public void checkPlayerInSight(Player player) {
        playerInSight = false;
        triangles.forEach(triangle -> {
            if (triangle.contains(player.getX(), player.getY())) {
                playerInSight = true;
            }
        });


        if (playerInSight) {
            canWalk = true;
            chase(player);
            shot();
        } else if (!isPlatrlling) {
            stop();
        }
    }

    private Point2D nextPoint() {
        return new Point2D(getX()+velocityX,getY()+velocityY);
    }


    //Vision
    public void calRays(List<Line> lines, double x, double y) { //Lista de rayos
        LinkedList<Line> ogRays = new LinkedList<>(); //Lista de rayos original
        LinkedList<Line> rays = new LinkedList<>(); //Lista de rayos fixed con colisiones
        LinkedList<Line> newRays = new LinkedList<>(); //Lista de rayos para almacenar los rayos modificados
        points = new ArrayList<>();

        double angle = 20;
        Point2D target = new Point2D(targetX, targetY);
        Point2D pos = new Point2D(getX(), getY());

        rays.add(createModifiedLine(new Line(getX(),getY(),targetX,targetY),0,1280));
        rays.add(createModifiedLine(rays.get(0),20,500));
        rays.add(createModifiedLine(rays.get(0),-20,500));
        ogRays.add(rays.get(0));
        ogRays.add(rays.get(1));
        ogRays.add(rays.get(2));
        fixLines(lines,rays);

        lines.forEach(line -> {  //Crea todos los rayos hacia las esquinas del mapa
            Point2D linePoint1 = new Point2D(line.getLayoutX() + line.getStartX(), line. getLayoutY() + line.getStartY());
            Point2D linePoint2 = new Point2D(line.getLayoutX() + line.getEndX(), line. getLayoutY() + line.getEndY());
            double pointAngle1 = pos.angle(target,linePoint1);
            double pointAngle2 =pos.angle(target,linePoint2);

            if (pointAngle1 <= angle) {
                rays.add(new Line(x,y,line.getLayoutX() + line.getStartX(), line. getLayoutY() + line.getStartY()));
                ogRays.add(new Line(x,y,line.getLayoutX() + line.getStartX(), line. getLayoutY() + line.getStartY()));
            }
            if (pointAngle2 <= angle) {
                rays.add(new Line(x,y,line.getLayoutX() + line.getEndX(), line.getLayoutY() + line.getEndY()));
                ogRays.add(new Line(x,y,line.getLayoutX() + line.getEndX(), line.getLayoutY() + line.getEndY()));
            }
        });


        fixLines(lines, rays); //Fixea los rayos tomando en cuenta las colisiones

        IntStream.rangeClosed(3, rays.size()-1).forEach(i -> {
            if (ogRays.get(i).getEndX() == rays.get(i).getEndX() && ogRays.get(i).getEndY() == rays.get(i).getEndY()) { //Verifica si el rayo llego a una esquina
                newRays.add(createModifiedLine(rays.get(i),2,500)); //Crea 2 rayos nuevos con un cambio de angulo de 5 grados y las añade a otra lista
                newRays.add(createModifiedLine(rays.get(i),-2,500));
            }
        });

        fixLines(lines, newRays); //Fixea los nuevos rayos modificados


        rays.addAll(newRays); //Se agregan a la lista original

        rays.forEach(ray -> points.add(new Point2D(ray.getEndX(), ray.getEndY()))); //Se almacenan todos los puntos en los cuales los rayos colisionan
    }

    private void fixLines(List<Line> lines, LinkedList<Line> newRays) { //Si el rayo intersecta con una linea hace que el rayo se corte en la interseccion
        lines.forEach(line ->
            newRays.forEach(ray -> {
                Point2D p = getRayCast(ray.getStartX(),ray.getStartY(),ray.getEndX(),ray.getEndY(),
                        line.getLayoutX() + line.getStartX(), line. getLayoutY() + line.getStartY(),
                        line.getLayoutX() + line.getEndX(), line. getLayoutY() + line.getEndY());
                ray.setEndX(p.getX());
                ray.setEndY(p.getY());
            }));
    }

    private Line createModifiedLine(Line originalLine, double angleChange, double additionalLength) {
        double startX = originalLine.getStartX();
        double startY = originalLine.getStartY();
        double endX = originalLine.getEndX();
        double endY = originalLine.getEndY();

        // Calcular la longitud original de la línea
        double originalLength = Math.hypot(endX - startX, endY - startY);

        // Calcular el ángulo original de la línea
        double originalAngle = Math.atan2(endY - startY, endX - startX);

        // Calcular las nuevas coordenadas con el cambio de ángulo y la longitud adicional
        double newEndX = startX + (originalLength + additionalLength) * Math.cos(originalAngle + Math.toRadians(angleChange));
        double newEndY = startY + (originalLength + additionalLength) * Math.sin(originalAngle + Math.toRadians(angleChange));

        return new Line(startX, startY, newEndX, newEndY);
    }

    public List<Polygon> updateTriangles() { //Crea los triangulos a partir de los puntos que formaron los rayos
        List<Polygon> triangles = new ArrayList<>();
        IntStream.rangeClosed(0,points.size()-1).forEach(i -> {
            Polygon polygon;
            if (i + 1 < points.size()) {
                polygon = new Polygon(getX(), getY(),
                        points.get(i).getX(), points.get(i).getY(),
                        points.get((i + 1)).getX(), points.get((i + 1)).getY());
                polygon.setOpacity(0.4);
                polygon.setStrokeWidth(0);
                triangles.add(polygon);

            }
        });

        return triangles;
    }

    public static Point2D getRayCast(double p0_x, double p0_y, double p1_x, double p1_y, double p2_x, double p2_y, double p3_x, double p3_y) { //Calcula el punto de interseccion entre 2 lineas
        double s1_x, s1_y, s2_x, s2_y;
        s1_x = p1_x - p0_x;
        s1_y = p1_y - p0_y;
        s2_x = p3_x - p2_x;
        s2_y = p3_y - p2_y;

        double s, t;
        double v = -s2_x * s1_y + s1_x * s2_y;
        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / v;
        t = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / v;

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            double x = p0_x + (t * s1_x);
            double y = p0_y + (t * s1_y);

            return new Point2D(x,y);
        }

        return new Point2D(p1_x,p1_y); // No collision
    }


    //Sort
    public void mergeSort(List<Point2D> arr) {
        int len = arr.size();
        if (len <= 1) return;

        int middle = len/2;
        List<Point2D> leftList = new ArrayList<>();
        List<Point2D> rightList = new ArrayList<>();

        IntStream.rangeClosed(0, len-1).forEach(i -> {
            if (i < middle) {
                leftList.add(arr.get(i));
            } else {
                rightList.add(arr.get(i));
            }
        });

        mergeSort(leftList);
        mergeSort(rightList);
        merge(leftList,rightList,arr);

    }

    private void merge(List<Point2D> left, List<Point2D> right, List<Point2D> arr) {
       int leftSize = arr.size()/2;
       int rightSize = arr.size() - leftSize;
       int i = 0, l = 0, r = 0;
       Line lineFloor = createModifiedLine(new Line(getX(),getY(),targetX,targetY),-20,500);
       Point2D point = new Point2D(getX(),getY());
       Point2D floor = new Point2D(lineFloor.getEndX(), lineFloor.getEndY());

       while (l < leftSize && r < rightSize) {
           double angleLeft = point.angle(floor, left.get(l));
           double angleRight = point.angle(floor, right.get(r));

           if (angleLeft < angleRight) {
               arr.set(i, left.get(l));
               i++;
               l++;
           } else {
               arr.set(i, right.get(r));
               i++;
               r++;
           }
       }

       while (l < leftSize) {
           arr.set(i, left.get(l));
           i++;
           l++;
       }
        while (r < rightSize) {
            arr.set(i, right.get(r));
            i++;
            r++;
        }
    }

    public List<Polygon> getTriangles() {
        return triangles;
    }

}
