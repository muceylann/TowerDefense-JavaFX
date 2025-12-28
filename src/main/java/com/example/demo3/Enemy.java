package com.example.demo3;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class EnemyManager {
    private final Pane enemyLayer;
    private final List<Point> path;
    private final double tileSize;
    private final List<Enemy> activeEnemies = new ArrayList<>();
    private Runnable enemyStateListener;
    private final AtomicInteger activeEnemyCount = new AtomicInteger(0);
    private Runnable onEnemyKilled;
    private Runnable onEnemyReachedEnd;



    public EnemyManager(List<Point> path, Pane enemyLayer, double tileSize) {
        this.path = path;
        this.enemyLayer = enemyLayer;
        this.tileSize = tileSize;
    }

    public boolean areAllEnemiesProcessed() {
        return activeEnemyCount.get() == 0;
    }

    public void spawnWave(int enemyCount, double intervalSec, double delayBeforeWaveSec) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < enemyCount; i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(delayBeforeWaveSec + i * intervalSec),
                    e -> spawnEnemy()
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.play();
    }

    public List<Enemy> getActiveEnemies() {
        return new ArrayList<>(activeEnemies);
    }

    public static ArrayList<Integer> getEnemyCounts(int level) {
        return readWaveData(level, 0);
    }

    public static ArrayList<Double> getIntervals(int level) {
        ArrayList<Double> intervals = new ArrayList<>();
        for (String[] parts : readWaveLines(level)) {
            intervals.add(Double.valueOf(Double.parseDouble(parts[1].trim())));
        }
        return intervals;
    }

    public static ArrayList<Integer> getDelays(int level) {
        ArrayList<Integer> delays = new ArrayList<>();
        for (String[] parts : readWaveLines(level)) {
            delays.add(Integer.valueOf(Integer.parseInt(parts[2].trim())));
        }
        return delays;
    }

    private static ArrayList<String[]> readWaveLines(int level) {
        ArrayList<String[]> waveLines = new ArrayList<>();
        String filename = "level" + level + ".txt";
        boolean startReading = false;

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("WAVE_DATA:")) {
                    startReading = true;
                    continue;
                }
                if (startReading && !line.isBlank()) {
                    waveLines.add(line.split(","));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Wave data file not found: " + filename);
            e.printStackTrace();
        }

        return waveLines;
    }

    private static ArrayList<Integer> readWaveData(int level, int index) {
        ArrayList<Integer> data = new ArrayList<>();
        for (String[] parts : readWaveLines(level)) {
            data.add(Integer.valueOf(Integer.parseInt(parts[index].trim())));
        }
        return data;
    }

    public void setEnemyStateListener(Runnable listener) {
        this.enemyStateListener = listener;
    }

    public void spawnEnemy() {
        Enemy enemy = new Enemy(path, enemyLayer, tileSize);
        activeEnemies.add(enemy);
        activeEnemyCount.incrementAndGet();

        enemy.setOnRemoved(() -> {
            Platform.runLater(() -> {
                activeEnemies.remove(enemy);
                activeEnemyCount.decrementAndGet();
                if (enemyStateListener != null) {
                    enemyStateListener.run();
                }
            });
        });
    }
    public void setOnEnemyKilled(Runnable listener) {
        this.onEnemyKilled = listener;
    }

    public class Enemy {
        private  Group body;
        private  Rectangle healthBar;
        private final double maxHealth = 30;
        private double currentHealth = 30;
        private boolean reachedEnd = false;
        private  PathTransition transition;
        private Runnable onRemoved;


        public Enemy(List<Point> path, Pane layer, double tileSize) {
            if (path.isEmpty()) return;

            // Enemy visualization
            Polygon triangle = new Polygon(0.0, 15.0, 20.0, 15.0, 10.0, 0.0);
            triangle.setFill(Color.DARKRED);

            Circle circle = new Circle(10, -5, 5);
            circle.setFill(Color.SADDLEBROWN);

            healthBar = new Rectangle(tileSize * 0.5, 5, Color.LIMEGREEN);
            healthBar.setTranslateX(-tileSize * 0.09);
            healthBar.setTranslateY(-tileSize * 0.5);

            body = new Group(triangle, circle, healthBar);
            layer.getChildren().add(body);

            // Create path for movement
            Path fxPath = createMovementPath(path, tileSize);
            double duration = calculatePathDuration(path, tileSize);

            transition = new PathTransition(Duration.seconds(duration), fxPath, body);
            transition.setInterpolator(javafx.animation.Interpolator.LINEAR);
            transition.setOnFinished(e -> {
                reachedEnd = true;
                removeFromGame(layer);
                if (onEnemyReachedEnd != null) {
                    onEnemyReachedEnd.run();
                }
            });

            transition.play();
        }

        private Path createMovementPath(List<Point> path, double tileSize) {
            Path fxPath = new Path();
            Point first = path.get(0);
            fxPath.getElements().add(new MoveTo(
                    first.y * tileSize + tileSize / 2,
                    first.x * tileSize + tileSize / 2
            ));

            for (int i = 1; i < path.size(); i++) {
                Point p = path.get(i);
                fxPath.getElements().add(new LineTo(
                        p.y * tileSize + tileSize / 2,
                        p.x * tileSize + tileSize / 2
                ));
            }
            return fxPath;
        }

        private double calculatePathDuration(List<Point> path, double tileSize) {
            double totalDistance = 0;
            for (int i = 0; i < path.size() - 1; i++) {
                Point a = path.get(i);
                Point b = path.get(i + 1);
                double dx = (b.y - a.y) * tileSize;
                double dy = (b.x - a.x) * tileSize;
                totalDistance += Math.hypot(dx, dy);
            }
            return totalDistance / 100; // speed factor
        }

        public double getCurrentHealth() {
            return currentHealth;
        }

        public void takeDamage(double damage) {
            currentHealth -= damage;
            if (currentHealth <= 0) {
                die();
            } else {
                updateHealthBar();
            }
        }

        private void updateHealthBar() {
            double ratio = currentHealth / maxHealth;
            healthBar.setWidth(tileSize * 0.6 * ratio);
        }

        private void die() {
            transition.stop();
            playExplosionEffect();
            if (onEnemyKilled != null) {
                onEnemyKilled.run();
            }

            removeFromGame((Pane) body.getParent());

        }

        private void removeFromGame(Pane layer) {
            if (layer != null) {
                layer.getChildren().remove(body);
            }
            if (onRemoved != null) {
                onRemoved.run();
            }
        }

        private void playExplosionEffect() {
            Parent parent = body.getParent();
            if (!(parent instanceof Pane pane)) return;

            int particleCount = 20;
            Random random = new Random();
            double x = body.getTranslateX();
            double y = body.getTranslateY();

            for (int i = 0; i < particleCount; i++) {
                Circle particle = new Circle(3, Color.ORANGE);
                particle.setTranslateX(x);
                particle.setTranslateY(y);
                pane.getChildren().add(particle);

                double angle = 2 * Math.PI * random.nextDouble();
                double dist = 30 + random.nextDouble() * 20;
                double dx = Math.cos(angle) * dist;
                double dy = Math.sin(angle) * dist;

                TranslateTransition explode = new TranslateTransition(Duration.millis(500), particle);
                explode.setByX(dx);
                explode.setByY(dy);
                explode.setOnFinished(e -> pane.getChildren().remove(particle));
                explode.play();
            }
        }

        public boolean hasReachedEnd() {
            return reachedEnd;
        }

        public Group getBody() {
            return body;
        }

        public void setOnRemoved(Runnable callback) {
            this.onRemoved = callback;
        }

    }
    public void setOnEnemyReachedEnd(Runnable callback) {
        this.onEnemyReachedEnd = callback;
    }

}
