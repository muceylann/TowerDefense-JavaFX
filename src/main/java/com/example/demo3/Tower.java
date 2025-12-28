package com.example.demo3;

import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Comparator;

public abstract class Tower extends Pane {
    protected int cost;
    protected double range;
    protected double fireRateMillis;
    protected double damage;
    protected int row;
    protected int col;
    protected long lastAttackTime=0;
    protected HelloApplication game;
    protected double tileSize;
    protected Pane gamePane;

    protected Group view;


    public Tower() {
    }

    public Tower(int row,int col, double damage, double range, int cost, double tileSize, Pane gamePane) {
        this.row = row;
        this.col = col;
        this.damage = damage;
        this.range = range;
        this.cost = cost;
        this.tileSize = tileSize;
        this.gamePane = gamePane;
    }
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean canBePlaced (GameMaps map) {
        if (row < 0 || row >= map.grid.length || col < 0 || col >= map.grid[0].length)
            return false;

        return !map.isPathCell(row, col);
    }

    public int getCost() {
        return this.cost;
    }
    public double getRange() {
        return this.range;
    }

    public void tryAttack(List<EnemyManager.Enemy> enemies) {
        if (!canAttackNow()) return;

        for (EnemyManager.Enemy enemy : enemies) {
            if (enemy.getCurrentHealth() <= 0 || enemy.hasReachedEnd()) continue;


            if (!enemy.hasReachedEnd() && isEnemyInRange(enemy)) {
                attack(enemy);
                lastAttackTime = System.currentTimeMillis();   //düzgün çalışan kontrol noktası
                break;
            }
        }

    }

    protected boolean canAttackNow() {
        long current= System.currentTimeMillis();
        return (current - lastAttackTime) >=fireRateMillis;
    }


    protected boolean isEnemyInRange(EnemyManager.Enemy enemy) {
        double enemyX = enemy.getBody().getTranslateX();
        double enemyY = enemy.getBody().getTranslateY();

        double towerX = (col + 0.5) * tileSize;
        double towerY = (row + 0.5) * tileSize;

        double dx = enemyX - towerX;
        double dy = enemyY - towerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= range * tileSize;
    }

    protected void attack(EnemyManager.Enemy enemy) {
        enemy.takeDamage(damage);
        showAttackEffect(enemy);
    }
    public abstract void render();
    public abstract void showAttackEffect(EnemyManager.Enemy enemy);

}

class SingleShotTower extends Tower {

    public SingleShotTower(int row, int col, double tileSize, Pane gamePane) {
        super(row, col, 10.0, 2.5, 50, tileSize, gamePane);
        this.fireRateMillis = 1000;
    }
    @Override
    public void render() {
        this.setManaged(false);
        this.setPrefSize(tileSize, tileSize);

        Image image = new Image(new File("TowerPng/single_shot.png").toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(tileSize);
        imageView.setFitHeight(tileSize);
        imageView.setPreserveRatio(false);
        imageView.setMouseTransparent(true);

        this.getChildren().add(imageView);

        this.setLayoutX(col * tileSize);
        this.setLayoutY(row * tileSize);

        gamePane.getChildren().add(this);
    }




    @Override
    public void showAttackEffect(EnemyManager.Enemy enemy) {

        Circle bullet = new Circle(tileSize * 0.1);
        bullet.setFill(Color.RED);

        double startX = (col + 0.5) * tileSize;
        double startY = (row + 0.5) * tileSize;

        bullet.setCenterX(startX);
        bullet.setCenterY(startY);

        gamePane.getChildren().add(bullet);

        final double[] x = {startX};
        final double[] y = {startY};
        double speed = 4.0;

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double targetX = enemy.getBody().getTranslateX();
                double targetY = enemy.getBody().getTranslateY();

                double dx = targetX - x[0];
                double dy = targetY - y[0];
                double distance = Math.sqrt(dx * dx + dy * dy);



                if (distance < 5 || enemy.hasReachedEnd() || enemy.getCurrentHealth() <= 0) {
                    gamePane.getChildren().remove(bullet);
                    stop();
                    return;
                }

                double nx = dx / distance;
                double ny = dy / distance;

                x[0] += nx * speed;
                y[0] += ny * speed;

                bullet.setCenterX(x[0]);
                bullet.setCenterY(y[0]);
            }
        };

        timer.start();
    }
}
class LaserTower extends Tower {
    private AnimationTimer laserTimer;

    public LaserTower(int row, int col, double tileSize, Pane gamePane) {
        super(row, col, 0.04, 2.5, 120, tileSize, gamePane);
        this.fireRateMillis=30;
    }
    @Override
    public void tryAttack(List<EnemyManager.Enemy> enemies) {
        for (EnemyManager.Enemy enemy : enemies) {
            if (!enemy.hasReachedEnd() && isEnemyInRange(enemy)) {
                showAttackEffect(enemy);
                enemy.takeDamage(damage);
            }
        }
    }

    @Override
    public void showAttackEffect(EnemyManager.Enemy enemy) {
        Line laser = new Line();
        laser.setStroke(Color.RED);
        laser.setStrokeWidth(3);
        laser.setOpacity(0.3);

        HelloApplication.overlayLayer.getChildren().add(laser);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //Kule yer değiştirdikten sonra düşman menzilde mi?
                if (enemy.hasReachedEnd() || enemy.getCurrentHealth() <= 0 ||
                        enemy.getBody().getParent() == null || !isEnemyInRange(enemy)) {
                    HelloApplication.overlayLayer.getChildren().remove(laser);
                    stop();
                    return;
                }

                double startX = (col + 0.5) * tileSize;
                double startY = (row + 0.5) * tileSize;

                Bounds enemyBounds = enemy.getBody().localToScene(enemy.getBody().getBoundsInLocal());
                Bounds overlayBounds = HelloApplication.overlayLayer.localToScene(HelloApplication.overlayLayer.getBoundsInLocal());

                double endX = enemyBounds.getMinX() - overlayBounds.getMinX();
                double endY = enemyBounds.getMinY() - overlayBounds.getMinY();

                laser.setStartX(startX);
                laser.setStartY(startY);
                laser.setEndX(endX);
                laser.setEndY(endY);
            }
        };

        timer.start();
    }




    @Override
    public void render() {
        this.setManaged(false);
        this.setPrefSize(tileSize, tileSize);

        Image image = new Image(new File("TowerPng/laser.png").toURI().toString());
        ImageView view = new ImageView(image);
        view.setFitWidth(tileSize);
        view.setFitHeight(tileSize);
        view.setPreserveRatio(false);
        view.setMouseTransparent(true);

        this.getChildren().add(view);
        this.setLayoutX(col * tileSize);
        this.setLayoutY(row * tileSize);
        gamePane.getChildren().add(this);
    }


}
class TripleShotTower extends Tower {
    public TripleShotTower(int row, int col, double tileSize, Pane gamePane) {
        super(row, col, 10.0, 2.5, 150, tileSize, gamePane);
        this.fireRateMillis=1000;
    }
    @Override
    public void tryAttack(List<EnemyManager.Enemy> enemies) {
        if (!canAttackNow()) return;

        List<EnemyManager.Enemy> targets = new ArrayList<>();
        for (EnemyManager.Enemy e : enemies) {
            if (!e.hasReachedEnd() && e.getCurrentHealth() > 0 && isEnemyInRange(e)) {
                targets.add(e);
            }
        }

        targets.sort(Comparator.comparingDouble(this::distanceTo));

        int count = 0;
        for (EnemyManager.Enemy target : targets) {
            if (target.getCurrentHealth() > 0) {
                showAttackEffect(target);
                target.takeDamage(damage);
                count++;
            }
            if (count == 3) break;
        }

        if (count > 0) {
            lastAttackTime = System.currentTimeMillis();
        }
    }

    public double distanceTo(EnemyManager.Enemy enemy) {
        double ex = enemy.getBody().getLayoutX();
        double ey = enemy.getBody().getLayoutY();
        double tx = (col + 0.5) * tileSize;
        double ty = (row + 0.5) * tileSize;
        return Math.hypot(ex - tx, ey - ty);
    }
    @Override
    public void showAttackEffect(EnemyManager.Enemy enemy) {
        Circle bullet = new Circle(tileSize * 0.1);
        bullet.setFill(Color.ORANGE);

        double startX = (col + 0.5) * tileSize;
        double startY = (row + 0.5) * tileSize;

        bullet.setCenterX(startX);
        bullet.setCenterY(startY);
        gamePane.getChildren().add(bullet);

        final double[] x = {startX};
        final double[] y = {startY};
        final EnemyManager.Enemy fixedTarget = enemy;
        double speed = 4.0;

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (fixedTarget == null || fixedTarget.getCurrentHealth() <= 0 || fixedTarget.hasReachedEnd()) {
                    gamePane.getChildren().remove(bullet);
                    stop();
                    return;
                }

                double targetX = fixedTarget.getBody().getTranslateX();
                double targetY = fixedTarget.getBody().getTranslateY();

                double dx = targetX - x[0];
                double dy = targetY - y[0];
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < 5) {
                    gamePane.getChildren().remove(bullet);
                    stop();
                    return;
                }

                double nx = dx / distance;
                double ny = dy / distance;

                x[0] += nx * speed;
                y[0] += ny * speed;

                bullet.setCenterX(x[0]);
                bullet.setCenterY(y[0]);
            }
        };

        timer.start();
    }

    @Override
    public void render() {
        this.setManaged(false);
        this.setPrefSize(tileSize, tileSize);

        Image image = new Image(new File("TowerPng/triple_shot.png").toURI().toString());
        ImageView view = new ImageView(image);
        view.setFitWidth(tileSize);
        view.setFitHeight(tileSize);
        view.setPreserveRatio(false);
        view.setMouseTransparent(true);

        this.getChildren().add(view);
        this.setLayoutX(col * tileSize);
        this.setLayoutY(row * tileSize);
        gamePane.getChildren().add(this);
    }


}

class MissileLauncherTower extends Tower {

    public MissileLauncherTower(int row, int col, double tileSize, Pane gamePane) {
        super(row, col, 30.0, 2.5, 200, tileSize, gamePane);
        this.fireRateMillis = 1000;
    }
    public void showAttackEffect(EnemyManager.Enemy enemy) {
    }
    @Override
    public void tryAttack(List<EnemyManager.Enemy> enemies) {
        if (!canAttackNow()) return;

        EnemyManager.Enemy mainTarget = null;
        double closestDistance = Double.MAX_VALUE;

        for (EnemyManager.Enemy enemy : enemies) {
            if (!enemy.hasReachedEnd() && isEnemyInRange(enemy)) {
                double distance = distanceTo(enemy);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    mainTarget = enemy;
                }
            }
        }

        if (mainTarget != null) {
            showMissileEffect(mainTarget, enemies);
            lastAttackTime = System.currentTimeMillis();
        }
    }
    public double distanceTo(EnemyManager.Enemy enemy) {
        double ex = enemy.getBody().getLayoutX();
        double ey = enemy.getBody().getLayoutY();
        double tx = (col + 0.5) * tileSize;
        double ty = (row + 0.5) * tileSize;
        return Math.hypot(ex - tx, ey - ty);
    }
    private double distanceBetween(EnemyManager.Enemy a, EnemyManager.Enemy b) {
        double ax = a.getBody().getLayoutX();
        double ay = a.getBody().getLayoutY();
        double bx = b.getBody().getLayoutX();
        double by = b.getBody().getLayoutY();
        return Math.hypot(ax - bx, ay - by);
    }
    public void showMissileEffect(EnemyManager.Enemy targetEnemy, List<EnemyManager.Enemy> allEnemies) {
        Circle missile = new Circle(tileSize * 0.20);
        missile.setFill(Color.DARKGRAY);

        double startX = (col + 0.5) * tileSize;
        double startY = (row + 0.5) * tileSize;

        missile.setCenterX(startX);
        missile.setCenterY(startY);
        gamePane.getChildren().add(missile);

        final double[] x = {startX};
        final double[] y = {startY};

        double speed = 4.0 / 1.5; // normalden daha yavaş

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double targetX = targetEnemy.getBody().getTranslateX();
                double targetY = targetEnemy.getBody().getTranslateY();

                double dx = targetX - x[0];
                double dy = targetY - y[0];
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < 5 || targetEnemy.hasReachedEnd() || targetEnemy.getCurrentHealth() <= 0) {
                    gamePane.getChildren().remove(missile);
                    applyAreaDamage(targetEnemy, allEnemies);
                    stop();
                    return;
                }

                double nx = dx / distance;
                double ny = dy / distance;

                x[0] += nx * speed;
                y[0] += ny * speed;

                missile.setCenterX(x[0]);
                missile.setCenterY(y[0]);
            }
        };

        timer.start();
    }
    public void applyAreaDamage(EnemyManager.Enemy center, List<EnemyManager.Enemy> enemies) {
        Bounds centerBounds = center.getBody().localToScene(center.getBody().getBoundsInLocal());
        double centerX = centerBounds.getMinX();
        double centerY = centerBounds.getMinY();

        for (EnemyManager.Enemy enemy : enemies) {
            if (enemy == center || enemy.hasReachedEnd() || enemy.getCurrentHealth() <= 0) continue;

            Bounds enemyBounds = enemy.getBody().localToScene(enemy.getBody().getBoundsInLocal());
            double ex = enemyBounds.getMinX();
            double ey = enemyBounds.getMinY();

            double dist = Math.hypot(ex - centerX, ey - centerY);
            if (dist <= tileSize * 1.8) {
                enemy.takeDamage(damage);
            }
        }

        center.takeDamage(damage);
    }

    @Override
    public void render() {
        this.setManaged(false);
        this.setPrefSize(tileSize, tileSize);

        Image image = new Image(new File("TowerPng/missile.png").toURI().toString());
        ImageView view = new ImageView(image);
        view.setFitWidth(tileSize);
        view.setFitHeight(tileSize);
        view.setPreserveRatio(false);
        view.setMouseTransparent(true);

        this.getChildren().add(view);
        this.setLayoutX(col * tileSize);
        this.setLayoutY(row * tileSize);
        gamePane.getChildren().add(this);
    }

}



