package com.example.demo3;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameMaps{

    private Color pathColor = Color.rgb(220, 220, 220);
    private Color lightYellowCell = Color.rgb(255, 255, 150);
    private Color darkYellowCell = Color.rgb(255, 220, 100);

    private double tileSize;
    private Rectangle rect;
    private int width;
    private int height;
    Cell grid[][];
    java.util.List<Point> pathcells;

    public GameMaps(int levelnumber){
        loadlevel(levelnumber);
        drawgrid();
    }

    public void loadlevel(int level){


        if(level == 1){
            try {
                File file = new File("level1.txt");
                Scanner scanner = new Scanner(file);
                pathcells = new ArrayList<>();

                int count = 0;
                while (scanner.hasNextLine()){

                    String line = scanner.nextLine();
                    if(count==0){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        width = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }
                    else if (count==1){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        height = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }

                    if (line.equals("WAVE_DATA:")) {
                        break;
                    }

                    if (!line.isEmpty()) {
                        String[] rc = line.split(",");
                        int row = Integer.parseInt(rc[0].trim());
                        int col = Integer.parseInt(rc[1].trim());
                        pathcells.add(new Point(row, col));
                    }
                }
                grid = new Cell[width][height];
                scanner.close();
            }catch (IOException e){
                System.out.println("Hata");
            }

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Cell cell = new Cell();

                    if (isPathCell(row, col)) {
                        cell.isPath = true;
                        cell.color = pathColor ;
                    } else {
                        cell.isPath = false;
                        int j = (int)(Math.random()*2);
                        if(j==0)
                            cell.color = darkYellowCell;
                        else
                            cell.color = lightYellowCell;
                    }

                    grid[row][col] = cell;
                }
            }

        }
        else if (level==2) {

            try {
                File file = new File("level2.txt");
                Scanner scanner = new Scanner(file);
                pathcells = new ArrayList<>();

                int count = 0;
                while (scanner.hasNextLine()){

                    String line = scanner.nextLine();
                    if(count==0){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        width = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }
                    else if (count==1){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        height = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }

                    if (line.equals("WAVE_DATA:")) {
                        break;
                    }

                    if (!line.isEmpty()) {
                        String[] rc = line.split(",");
                        int row = Integer.parseInt(rc[0].trim());
                        int col = Integer.parseInt(rc[1].trim());
                        pathcells.add(new Point(row, col));
                    }
                }
                grid = new Cell[width][height];
                scanner.close();
            }catch (IOException e){
                System.out.println("Hata");
            }

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Cell cell = new Cell();

                    if (isPathCell(row, col)) {
                        cell.isPath = true;
                        cell.color = pathColor ;
                    } else {
                        cell.isPath = false;
                        int j = (int)(Math.random()*2);
                        if(j==0)
                            cell.color = darkYellowCell;
                        else
                            cell.color = lightYellowCell;
                    }

                    grid[row][col] = cell;
                }
            }
        }

        else if (level==3){

            try {
                File file = new File("level3.txt");
                Scanner scanner = new Scanner(file);
                pathcells = new ArrayList<>();

                int count = 0;
                while (scanner.hasNextLine()){

                    String line = scanner.nextLine();
                    if(count==0){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        width = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }
                    else if (count==1){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        height = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }

                    if (line.equals("WAVE_DATA:")) {
                        break;
                    }

                    if (!line.isEmpty()) {
                        String[] rc = line.split(",");
                        int row = Integer.parseInt(rc[0].trim());
                        int col = Integer.parseInt(rc[1].trim());
                        pathcells.add(new Point(row, col));
                    }
                }
                grid = new Cell[width][height];
                scanner.close();
            }catch (IOException e){
                System.out.println("Hata");
            }

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Cell cell = new Cell();

                    if (isPathCell(row, col)) {
                        cell.isPath = true;
                        cell.color = pathColor ;
                    } else {
                        cell.isPath = false;
                        int j = (int)(Math.random()*2);
                        if(j==0)
                            cell.color = darkYellowCell;
                        else
                            cell.color = lightYellowCell;
                    }

                    grid[row][col] = cell;
                }
            }

        }

        else if (level==4) {

            try {
                File file = new File("level4.txt");
                Scanner scanner = new Scanner(file);
                pathcells = new ArrayList<>();

                int count = 0;
                while (scanner.hasNextLine()){

                    String line = scanner.nextLine();
                    if(count==0){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        width = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }
                    else if (count==1){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        height = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }

                    if (line.equals("WAVE_DATA:")) {
                        break;
                    }

                    if (!line.isEmpty()) {
                        String[] rc = line.split(",");
                        int row = Integer.parseInt(rc[0].trim());
                        int col = Integer.parseInt(rc[1].trim());
                        pathcells.add(new Point(row, col));
                    }
                }
                grid = new Cell[width][height];
                scanner.close();
            }catch (IOException e){
                System.out.println("Hata");
            }

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Cell cell = new Cell();

                    if (isPathCell(row, col)) {
                        cell.isPath = true;
                        cell.color = pathColor ;
                    } else {
                        cell.isPath = false;
                        int j = (int)(Math.random()*2);
                        if(j==0)
                            cell.color = darkYellowCell;
                        else
                            cell.color = lightYellowCell;
                    }

                    grid[row][col] = cell;
                }
            }

        }

        else if (level==5) {

            try {
                File file = new File("level5.txt");
                Scanner scanner = new Scanner(file);
                pathcells = new ArrayList<>();

                int count = 0;
                while (scanner.hasNextLine()){

                    String line = scanner.nextLine();
                    if(count==0){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        width = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }
                    else if (count==1){
                        String[] parts = line.split(":");
                        String widthValue = parts[1].trim();
                        height = Integer.parseInt(widthValue);
                        ++count;
                        continue;
                    }

                    if (line.equals("WAVE_DATA:")) {
                        break;
                    }

                    if (!line.isEmpty()) {
                        String[] rc = line.split(",");
                        int row = Integer.parseInt(rc[0].trim());
                        int col = Integer.parseInt(rc[1].trim());
                        pathcells.add(new Point(row, col));
                    }
                }
                grid = new Cell[width][height];
                scanner.close();
            }catch (IOException e){
                System.out.println("Hata");
            }

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Cell cell = new Cell();

                    if (isPathCell(row, col)) {
                        cell.isPath = true;
                        cell.color = pathColor ;
                    } else {
                        cell.isPath = false;
                        int j = (int)(Math.random()*2);
                        if(j==0)
                            cell.color = darkYellowCell;
                        else
                            cell.color = lightYellowCell;
                    }

                    grid[row][col] = cell;
                }
            }
        }
    }


    public boolean isPathCell(int row, int col) {
        for (int i = 0; i < pathcells.size(); i++) {
            Point cell = pathcells.get(i);
            if ((cell.x == row) && (cell.y == col)) {
                return true;
            }
        }
        return false;
    }

    public Rectangle getRect() {
        return rect;
    }

    public static class Cell{
        public boolean isPath;
        public Color color;

    }

    public GridPane drawgrid(){

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        for(int i=0 ; i<height ; i++){
            for (int k=0 ; k<width ; k++){
                rect = new Rectangle();

                if(height==10){
                    rect.setWidth(55);
                    rect.setHeight(55);
                } else {
                    rect.setWidth(40);
                    rect.setHeight(40);
                }

                tileSize = rect.getWidth();
                rect.setFill(grid[i][k].color);
                rect.setStroke(Color.WHITE);
                rect.setStrokeWidth(1);
                gridPane.add(rect,k,i);
            }
        }

        animategridload(gridPane);

        return gridPane;
    }

    public double getTileSize() {
        return tileSize;
    }

    public void animategridload(GridPane gridPane) {

        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            Node node = gridPane.getChildren().get(i);

            if (node instanceof Rectangle) {
                Rectangle rect = (Rectangle) node;
                rect.setScaleX(0);
                rect.setScaleY(0);
            }
        }


        List<Node> nodes = new ArrayList<>(gridPane.getChildren());
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof Rectangle) {
                Rectangle rect = (Rectangle) node;

                Integer colindex = GridPane.getColumnIndex(rect);
                if(colindex==null)
                    colindex= (Integer) 0;
                Integer rowindex = GridPane.getRowIndex(rect);
                if (rowindex==null)
                    rowindex= (Integer) 0;

                double delay = (2*rowindex+colindex)*50;

                ScaleTransition scaleTr = new ScaleTransition(Duration.millis(300), rect);
                scaleTr.setFromX(0);
                scaleTr.setFromY(0);
                scaleTr.setToX(1);
                scaleTr.setToY(1);
                scaleTr.setDelay(Duration.millis(delay));
                scaleTr.play();

            }
        }
    }

}
