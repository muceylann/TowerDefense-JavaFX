//package com.example.demo3;
//
//
//import javafx.animation.*;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.geometry.Insets;
//import javafx.geometry.Point2D;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.ClipboardContent;
//import javafx.scene.input.Dragboard;
//import javafx.scene.input.TransferMode;
//import javafx.scene.layout.*;
//import javafx.scene.shape.*;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//import javafx.scene.paint.Color;
//import javafx.scene.image.Image;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class Game extends Application {
//    private List<Tower> towers = new ArrayList<>();
//    private Pane towerLayer;
//    private double tileSize;
//    private int money = 100;
//    protected Pane gamePane;
//    private Stage primaryStage;
//    private int Totallevels = 5;
//    private int currentlevel = 1;
//    private AnimationTimer gameLoop;
//    private Text livesText;
//    private Text moneyText;
//    private Text nextWaveText;
//    private int lives = 5;
//    private int countdownSeconds = 15;
//    private Timeline countdownTimeline;
//    private WaveManager waveManager;
//    private Circle rangeCircle;
//    private double dragOffsetX, dragOffsetY;
//    public static Pane overlayLayer;
//
//
//
//
//
//    @Override
//    public void start(Stage primaryStage) throws IOException {
//        this.primaryStage = primaryStage;
//        showMainMenu();
//        primaryStage.setTitle("Tower Defense Game");
//        primaryStage.show();
//    }
//
//    public void showMainMenu() {
//        Button startbut = new Button("Start Game");
//        startbut.setPrefSize(200, 100);
//        startbut.setBackground(new Background(new BackgroundFill(Color.rgb(255, 220, 100), CornerRadii.EMPTY, Insets.EMPTY)));
//        startbut.setFont(Font.font("Arial", 25));
//
//        startbut.setOnAction(actionEvent -> {
//            resetGame();
//            currentlevel = 1;
//            showGameLevel();
//        });
//
//        VBox vBox = new VBox(20);
//        vBox.getChildren().addAll(startbut);
//        vBox.setAlignment(Pos.CENTER);
//        Scene scene = new Scene(vBox, 1500, 800);
//        primaryStage.setScene(scene);
//    }
//
//    public void showGameLevel() {
//        resetGame();
//
//        GameMaps gamemap = new GameMaps(currentlevel);
//        GridPane pane = gamemap.drawgrid();
//        this.tileSize = gamemap.getTileSize();
//
//        Pane enemyLayer = new Pane();
//        enemyLayer.setMouseTransparent(true);
//        overlayLayer = new Pane();
//        overlayLayer.setPickOnBounds(false);
//        overlayLayer.setMouseTransparent(true);
//
//        rangeCircle = new Circle();
//        rangeCircle.setStroke(Color.RED);
//        rangeCircle.setFill(Color.color(1, 0, 0, 0.4));
//        rangeCircle.setVisible(false);
//        rangeCircle.setManaged(false);
//
//        overlayLayer.getChildren().add(rangeCircle);
//
//
//        towerLayer = new Pane();
//        StackPane mapStack = new StackPane(pane, towerLayer, enemyLayer, overlayLayer);
//        mapStack.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
//        mapStack.setAlignment(Pos.TOP_LEFT);
//
//        EnemyManager manager = new EnemyManager(gamemap.pathcells, enemyLayer, tileSize);
//        manager.setOnEnemyReachedEnd(() -> {
//            lives--;
//            Platform.runLater(() -> livesText.setText("Lives: " + lives));
//            if (lives <= 0) {
//                Platform.runLater(() -> showLoseScreen());
//            }
//        });
//
//        manager.setOnEnemyKilled(() -> {
//            money += 10;
//            moneyText.setText("Money: " + money + "$");
//        });
//
//        gameLoop = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                List<EnemyManager.Enemy> activeEnemies = manager.getActiveEnemies();
//                for (Tower tower : towers) {
//                    tower.tryAttack(activeEnemies);
//                }
//            }
//        };
//        gameLoop.start();
//
//        livesText = new Text("Lives: " + lives);
//        moneyText = new Text("Money: " + money + "$");
//        nextWaveText = new Text("Next Wave:");
//
//        livesText.setFont(Font.font(18));
//        moneyText.setFont(Font.font(18));
//        nextWaveText.setFont(Font.font(18));
//
//        VBox towerButtons = new VBox(20);
//        towerButtons.setPadding(new Insets(80,20,20,20));
//        towerButtons.setAlignment(Pos.TOP_CENTER);
//        towerButtons.setPrefWidth(250);
//
//        towerButtons.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
//        towerButtons.getChildren().addAll(livesText, moneyText, nextWaveText);
//
//        TowerType[] towerTypes = TowerType.values();
//        String[] names = {
//                "Single Shot Tower - 50$", "Laser Tower - 120$", "Triple Shot Tower - 150$", "Missile Launcher Tower - 200$"
//        };
//
//        for (int i = 0; i < towerTypes.length; i++) {
//            TowerType type = towerTypes[i];
//
//
//            Image image = new Image(new File("TowerPng/" + type.name().toLowerCase() + ".png").toURI().toString());
//            ImageView imageView = new ImageView(image);
//            imageView.setFitWidth(30);
//            imageView.setFitHeight(30);
//
//
//            Text label = new Text(names[i]);
//            label.setFont(Font.font("Arial", 14));
//
//
//            HBox content = new HBox(10, imageView, label);
//            content.setAlignment(Pos.CENTER_LEFT);
//
//
//            Button btn = new Button();
//            btn.setGraphic(content);
//            btn.setUserData(type);
//            btn.setPrefWidth(220);
//            btn.setPrefHeight(40);
//            btn.setStyle("-fx-background-color: #ffdc64; -fx-font-weight: bold;");
//
//
//            btn.setOnDragDetected(e -> {
//                Dragboard db = btn.startDragAndDrop(TransferMode.COPY);
//                ClipboardContent contentData = new ClipboardContent();
//                contentData.putString(type.toString());
//                db.setContent(contentData);
//                e.consume();
//            });
//
//
//            towerButtons.getChildren().add(btn);
//        }
//
//
//        mapStack.setOnDragOver(e -> {
//            if (e.getDragboard().hasString()) {
//                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
//
//                double mouseX = e.getX();
//                double mouseY = e.getY();
//                int col = (int)(mouseX / tileSize);
//                int row = (int)(mouseY / tileSize);
//
//                double range = 2.5 * tileSize;
//                rangeCircle.setRadius(range);
//                rangeCircle.setCenterX((col + 0.5) * tileSize);
//                rangeCircle.setCenterY((row + 0.5) * tileSize);
//                rangeCircle.setVisible(true);
//
//            }
//            e.consume();
//        });
//        mapStack.setOnDragExited(e -> {
//            rangeCircle.setVisible(false);
//        });
//
//
//        mapStack.setOnDragDropped(e -> {
//            Dragboard db = e.getDragboard();
//            boolean success = false;
//            rangeCircle.setVisible(false);
//
//            if (db.hasString()) {
//                TowerType type = TowerType.valueOf(db.getString());
//                int col = (int)(e.getX() / tileSize);
//                int row = (int)(e.getY() / tileSize);
//
//                if (canBePlaced(gamemap, row, col) && money >= getCost(type)) {
//                    Tower tower = createTower(type, row, col, tileSize, towerLayer);
//                    tower.render();
//                    towers.add(tower);
//                    money -= tower.getCost();
//                    moneyText.setText("Money: "+ money + "$");
//                    success = true;
//
//                    tower.setOnMousePressed(ev -> {
//                        Point2D mouseInParent = towerLayer.sceneToLocal(ev.getSceneX(), ev.getSceneY());
//                        dragOffsetX = mouseInParent.getX() - tower.getLayoutX();
//                        dragOffsetY = mouseInParent.getY() - tower.getLayoutY();
//                    });
//
//                    tower.setOnMouseDragged(ev -> {
//                        Point2D mouseInParent = towerLayer.sceneToLocal(ev.getSceneX(), ev.getSceneY());
//
//                        double newX = mouseInParent.getX() - dragOffsetX;
//                        double newY = mouseInParent.getY() - dragOffsetY;
//
//                        tower.setLayoutX(newX);
//                        tower.setLayoutY(newY);
//
//                        rangeCircle.setRadius(tower.getRange() * tileSize);
//                        rangeCircle.setCenterX(newX + tileSize / 2);
//                        rangeCircle.setCenterY(newY + tileSize / 2);
//                        rangeCircle.setVisible(true);
//                    });
//
//                    tower.setOnMouseReleased(ev -> {
//                        Point2D mouseInParent = towerLayer.sceneToLocal(ev.getSceneX(), ev.getSceneY());
//                        int newCol = (int)(mouseInParent.getX() / tileSize);
//                        int newRow = (int)(mouseInParent.getY() / tileSize);
//
//                        rangeCircle.setVisible(false);
//
//                        if (newCol < 0 || newRow < 0 || newCol >= gamemap.grid[0].length || newRow >= gamemap.grid.length) {
//                            towerLayer.getChildren().remove(tower);
//                            towers.remove(tower);
//                            money += tower.getCost();
//                            moneyText.setText("Money: " + money + "$");
//                        } else if (canBePlaced(gamemap, newRow, newCol)) {
//                            tower.setRow(newRow);
//                            tower.setCol(newCol);
//                            tower.setLayoutX(newCol * tileSize);
//                            tower.setLayoutY(newRow * tileSize);
//                        } else {
//                            tower.setLayoutX(tower.getCol() * tileSize);
//                            tower.setLayoutY(tower.getRow() * tileSize);
//                        }
//                    });
//                }
//            }
//
//            e.setDropCompleted(success);
//            e.consume();
//        });
//
//        BorderPane mainLayout = new BorderPane();
//        mainLayout.setCenter(mapStack);
//        mainLayout.setRight(towerButtons);
//
//        Scene scene = new Scene(mainLayout, 1500, 800);
//        primaryStage.setScene(scene);
//
//        waveManager = new WaveManager(currentlevel, manager, nextWaveText, () -> {
//            if (lives > 0) {
//                gameLoop.stop();
//                showWinScreen();
//            }
//        });
//        waveManager.start();
//    }
//
//    private void showWinScreen() {
//        Text resultText;
//        Button nextActionButton;
//
//        if (currentlevel >= Totallevels) {
//            //Tüm seviyeler geçildi
//            resultText = new Text("Congratulations! You beat all levels!");
//            resultText.setFont(Font.font("Arial", 24));
//
//            nextActionButton = new Button("Back to Main Menu");
//            nextActionButton.setPrefSize(300, 70);
//            nextActionButton.setBackground(new Background(new BackgroundFill(Color.rgb(255, 220, 100), CornerRadii.EMPTY, Insets.EMPTY)));
//            nextActionButton.setFont(Font.font("Arial", 25));
//
//            nextActionButton.setOnAction(event -> {
//                currentlevel = 1;
//                resetGame();
//                showMainMenu();
//            });
//        } else {
//            // Normal seviye geçildi
//            resultText = new Text("You Won");
//            resultText.setFont(Font.font("Arial", 24));
//
//            nextActionButton = new Button("Continue to Next Level");
//            nextActionButton.setPrefSize(300, 70);
//            nextActionButton.setBackground(new Background(new BackgroundFill(Color.rgb(255, 220, 100), CornerRadii.EMPTY, Insets.EMPTY)));
//            nextActionButton.setFont(Font.font("Arial", 25));
//
//            nextActionButton.setOnAction(event -> {
//                resetGame();
//                currentlevel++;
//                showGameLevel();
//            });
//        }
//
//        VBox vBox = new VBox(20, resultText, nextActionButton);
//        vBox.setAlignment(Pos.CENTER);
//        vBox.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
//
//        Scene scene = new Scene(vBox, 1500, 800);
//        primaryStage.setScene(scene);
//    }
//
//
//    private void showLoseScreen() {
//        if (gameLoop != null) gameLoop.stop();
//        if (waveManager != null) {
//            waveManager.setGameOver(true);
//            if (waveManager.getCountdownTimeline() != null) {
//                waveManager.getCountdownTimeline().stop();
//            }
//            waveManager = null;
//        }
//
//
//        Text gameOverText = new Text("Game Over!");
//        gameOverText.setFont(Font.font("Arial", 32));
//        gameOverText.setFill(Color.DARKRED);
//
//        Button backToMenuButton = new Button("Back to Main Menu");
//        backToMenuButton.setFont(Font.font("Arial", 24));
//        backToMenuButton.setPrefSize(300, 70);
//        backToMenuButton.setBackground(new Background(new BackgroundFill(Color.rgb(255, 220, 100), CornerRadii.EMPTY, Insets.EMPTY)));
//
//
//        backToMenuButton.setOnAction(event -> {
//            resetGame();
//            currentlevel = 1;
//            showMainMenu();
//        });
//
//        VBox vbox = new VBox(20, gameOverText, backToMenuButton);
//        vbox.setAlignment(Pos.CENTER);
//        vbox.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
//
//        Scene scene = new Scene(vbox, 1500, 800);
//        primaryStage.setScene(scene);
//    }
//
//    private void resetGame() {
//        if (gameLoop != null) {
//            gameLoop.stop();
//            gameLoop = null;
//        }
//        if (waveManager != null && waveManager.getCountdownTimeline() != null) {
//            waveManager.getCountdownTimeline().stop();
//            waveManager = null;
//        }
//        towers.clear();
//        if (towerLayer != null) towerLayer.getChildren().clear();
//        money = 100;
//        lives = 5;
//    }
//
//    private boolean canBePlaced(GameMaps map, int row, int col) {
//        if (row < 0 || col < 0 || row >= map.grid.length || col >= map.grid[0].length)
//            return false;
//
//        if (map.isPathCell(row, col))
//            return false;
//
//        // AYNI KAREDEKİ KULE KONTROLÜ
//        for (Tower t : towers) {
//            if (t.getRow() == row && t.getCol() == col) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    private int getCost(TowerType type) {
//        return switch (type) {
//            case SINGLE_SHOT -> 50;
//            case LASER -> 120;
//            case TRIPLE_SHOT -> 150;
//            case MISSILE -> 200;
//        };
//    }
//
//    private Tower createTower(TowerType type, int row, int col, double tileSize, Pane layer) {
//        return switch (type) {
//            case SINGLE_SHOT -> new SingleShotTower(row, col, tileSize, layer);
//            case LASER -> new LaserTower(row, col, tileSize, layer);
//            case TRIPLE_SHOT -> new TripleShotTower(row, col, tileSize, layer);
//            case MISSILE -> new MissileLauncherTower(row, col, tileSize, layer);
//        };
//    }
//
//    public static void main(String[] args) {
//        launch();
//    }
//}
//
//
