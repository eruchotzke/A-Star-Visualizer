package application;

import Mazes.MazeGenerator;
import com.sun.org.apache.xerces.internal.impl.dv.xs.YearDV;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.*;
import pathfinding.*;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    /* Graphics Resources */
    private Color baseColor = Color.WHITE;
    private Color targetColor = Color.RED;
    private Color sourceColor = Color.BLUE;
    private Color obstacleColor = Color.BLACK;
    private Color pathColor = Color.YELLOW;
    private Color closedColor = Color.CYAN;
    private Color observedColor = Color.LIGHTBLUE;

    /* Model Resources */
    private static int X_DIMENSION = 12;
    private static int Y_DIMENSION;
    private static double aspectRatio;
    Grid dGrid;
    Grid aGrid;
    Path dShort;
    Path aShort;
    private PathfinderState dState;
    private PathfinderState aState;
    private AnimationTimer dAnimator;
    private AnimationTimer aAnimator;

    /*Serialized Resources */
    @FXML
    private Canvas canvas_d;

    @FXML
    private Canvas canvas_a;

    @FXML
    private ChoiceBox clickMode;

    @FXML
    private Label densityLabel;

    @FXML
    private Slider densitySlider;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aspectRatio = canvas_a.getWidth() / canvas_a.getHeight();
        onSliderChanged();
        dGrid = new Grid(X_DIMENSION, Y_DIMENSION);
        aGrid = new Grid(X_DIMENSION, Y_DIMENSION);

        clickMode.getItems().add("NONE");
        clickMode.getItems().add("TOGGLE OBSTACLE");
        clickMode.getItems().add("SET SOURCE");
        clickMode.getItems().add("SET TARGET");
        clickMode.setValue(clickMode.getItems().get(0));

        MazeGenerator.generatePrimsMaze(aGrid);
        dGrid.copyGrid(aGrid);

        drawGridLines(canvas_d, dGrid, true);
        drawGridLines(canvas_a, aGrid, true);
    }

    public void onSliderChanged(){
        densityLabel.setText("Grid Density: " + (int)(densitySlider.getValue()));
        X_DIMENSION = (int)(densitySlider.getValue());
        Y_DIMENSION = (int)(X_DIMENSION / aspectRatio);
    }

    public void regenerateMaze(){
        dGrid = new Grid(X_DIMENSION, Y_DIMENSION);
        aGrid = new Grid(X_DIMENSION, Y_DIMENSION);
        MazeGenerator.generatePrimsMaze(aGrid);
        dGrid.copyGrid(aGrid);
        drawGridLines(canvas_d, dGrid, true);
        drawGridLines(canvas_a, aGrid, true);
    }

    public void onDCanvasClick(MouseEvent event){
        Tile selected = getTileAtDCoordinates(event);
        handleMouseClick(selected);
    }

    public void onACanvasClick(MouseEvent event){
        Tile selected = getTileAtACoordinates(event);
        handleMouseClick(selected);
    }

    private void handleMouseClick(Tile selected) {
        switch(((String)(clickMode.getValue())).toLowerCase()){
            case "none":
                System.out.println(selected);
                break;
            case "toggle obstacle":
                Tile aTile = aGrid.getTileAt(selected.x, selected.y);
                Tile dTile = dGrid.getTileAt(selected.x, selected.y);

                aTile.isPassable = !aTile.isPassable;
                dTile.isPassable = !dTile.isPassable;
                break;
            case "set source":
                aGrid.setSource(selected.x, selected.y);
                dGrid.setSource(selected.x, selected.y);
                break;
            case "set target":
                aGrid.setTarget(selected.x, selected.y);
                dGrid.setTarget(selected.x, selected.y);
                break;
            default:
                System.out.println("Command not found.");
                break;
        }
        drawGridLines(canvas_a, aGrid, true);
        drawGridLines(canvas_d, dGrid, true);
    }

    public Tile getTileAtDCoordinates(MouseEvent event){
        /* First get the intervals for square drawing */
        double xOffset = canvas_d.getWidth() / dGrid.getXDimension();
        double yOffset = canvas_d.getHeight() / dGrid.getYDimension();

        /* Now divide the coordinates to get the integer value */
        int x = (int)Math.floor(event.getX() / xOffset);
        int y = (int)Math.floor(event.getY() / yOffset);

        return dGrid.getTileAt(x, y);
    }

    public Tile getTileAtACoordinates(MouseEvent event){
        /* First get the intervals for square drawing */
        double xOffset = canvas_a.getWidth() / aGrid.getXDimension();
        double yOffset = canvas_a.getHeight() / aGrid.getYDimension();

        /* Now divide the coordinates to get the integer value */
        int x = (int)Math.floor(event.getX() / xOffset);
        int y = (int)Math.floor(event.getY() / yOffset);

        return aGrid.getTileAt(x, y);
    }

    public void generateAStarPath(){
        AStarPathfinder pathfinder = new AStarPathfinder();
        aShort = pathfinder.generateShortestPath(aGrid);
        System.out.println(aShort);
        drawGridLines(canvas_a, aGrid, true);
    }

    public void generateShortestPath(){
        DjikstraPathfinder pathfinder = new DjikstraPathfinder();
        if(dAnimator != null){
            dAnimator.stop();
            dAnimator = null;
        }
        PathfinderState state = new PathfinderState(dGrid, true);
        while(!state.isComplete){
            state = pathfinder.incrementShortestPath(dGrid, state);
            drawDGrid(canvas_d, dGrid, state);
        }
        drawDGrid(canvas_d, dGrid, state);

        generateAStarPath();
    }

    private void incrementShortestPath(){
        DjikstraPathfinder pathfinder = new DjikstraPathfinder();
        if(dState == null) dState = new PathfinderState(dGrid, true);

        dState = pathfinder.incrementShortestPath(dGrid, dState);
        drawDGrid(canvas_d, dGrid, dState);

        if(dState.isComplete) dState = null;
    }

    private void incrementAStarShortestPath(){
        AStarPathfinder pathfinder1 = new AStarPathfinder();
        if(aState == null) aState = new PathfinderState(aGrid, false);

        aState = pathfinder1.incrementShortestPath(aGrid, aState);
        drawAGrid(canvas_a, aGrid, aState);

        if(aState.isComplete) aState = null;
    }

    public void animateShortestPaths(){
        if(aGrid.getSource() == null || aGrid.getTarget() == null){
            Random rand = new Random();
            int x = rand.nextInt(aGrid.getXDimension());
            int y = rand.nextInt(aGrid.getYDimension());
            while(!aGrid.getTileAt(x,y).isPassable){
                x = rand.nextInt(aGrid.getXDimension());
                y = rand.nextInt(aGrid.getYDimension());
            }
            aGrid.setSource(x,y);
            dGrid.setSource(x,y);

            x = rand.nextInt(aGrid.getXDimension());
            y = rand.nextInt(aGrid.getYDimension());

            while(!aGrid.getTileAt(x,y).isPassable && aGrid.getTileAt(x,y) == aGrid.getSource()){
                x = rand.nextInt(aGrid.getXDimension());
                y = rand.nextInt(aGrid.getYDimension());
            }
            aGrid.setTarget(x,y);
            dGrid.setTarget(x,y);
        }
        System.out.println(aGrid.getTarget() + " " + aGrid.getSource());
        animateShortestPath();
        animateAStarShortestPath();
    }

    private void animateShortestPath(){
        if(dAnimator != null){
            dAnimator.stop();
            dState = null;
        }
        dAnimator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                incrementShortestPath();
                if(dState == null){
                    dAnimator.stop();
                    dAnimator = null;
                }
            }
        };

        dAnimator.start();
    }

    private void animateAStarShortestPath(){
        if(aAnimator != null){
            aAnimator.stop();
            aState = null;
        }
        aAnimator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                incrementAStarShortestPath();
                if(aState == null){
                    aAnimator.stop();
                    aAnimator = null;
                }
            }
        };

        aAnimator.start();
    }

    private void drawAGrid(Canvas canvas, Grid grid, PathfinderState state){
        drawGridLines(canvas, grid, false);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        /* Get the offsets */
        double xOffset = canvas.getWidth() / grid.getXDimension();
        double yOffset = canvas.getHeight() / grid.getYDimension();

        /* Color each box */
        double currX = 0;
        double currY = 0;
        for(int x = 0; x < grid.getXDimension(); x++){
            currY = 0;
            for(int y = 0; y < grid.getYDimension(); y++){
                Tile curr = grid.getTileAt(x, y);
                //color the tile according to what it is
                gc.setFill(baseColor);
                if(state.open.contains(new AStarWrapper(curr))) gc.setFill(observedColor);
                if(state.star_closed.contains(new AStarWrapper(curr))) gc.setFill(closedColor);
                if(state.star_path.contains(new AStarWrapper(curr))) gc.setFill(pathColor);
                if(!curr.isPassable) gc.setFill(obstacleColor);
                if(aGrid.getSource() == curr) gc.setFill(sourceColor);
                if(aGrid.getTarget() == curr) gc.setFill(targetColor);
                gc.fillRect(currX, currY, xOffset, yOffset);
                currY += yOffset;
            }
            currX += xOffset;
        }
    }

    private void drawDGrid(Canvas canvas, Grid grid, PathfinderState state){
        drawGridLines(canvas, grid, false);

        /* Now color tiles according to the dState */
        GraphicsContext gc = canvas.getGraphicsContext2D();

        /* Get the offsets */
        double xOffset = canvas.getWidth() / grid.getXDimension();
        double yOffset = canvas.getHeight() / grid.getYDimension();

        /* Color each box */
        double currX = 0;
        double currY = 0;
        for(int x = 0; x < grid.getXDimension(); x++){
            currY = 0;
            for(int y = 0; y < grid.getYDimension(); y++){
                Tile curr = grid.getTileAt(x, y);
                //color the tile according to what it is
                gc.setFill(baseColor);
                if(state.observed.contains(curr)) gc.setFill(observedColor);
                if(state.closed.contains(curr)) gc.setFill(closedColor);
                if(state.shortestPath.getPath().contains(curr)) gc.setFill(pathColor);
                if(!curr.isPassable) gc.setFill(obstacleColor);
                if(dGrid.getSource() == curr) gc.setFill(sourceColor);
                if(dGrid.getTarget() == curr) gc.setFill(targetColor);
                gc.fillRect(currX, currY, xOffset, yOffset);
                currY += yOffset;
            }
            currX += xOffset;
        }
    }


    /**
     * Draw the grid outline onto a canvas.
     * @param canvas The canvas to draw the grid onto.
     * @param grid The grid to draw onto the canvas.
     */
    private void drawGridLines(Canvas canvas, Grid grid, boolean color){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);

        /* First get the intervals for line drawing */
        double xOffset = canvas.getWidth() / grid.getXDimension();
        double yOffset = canvas.getHeight() / grid.getYDimension();

        /* Now draw lines for the x axis grid */
        double offset = 0;
        for(int i = 0; i < grid.getXDimension(); i++){
            gc.strokeLine(offset, 0, offset, canvas.getHeight());
            offset += xOffset;
        }

        /* Now draw lines for the y axis grid */
        offset = 0;
        for(int i = 0; i < grid.getYDimension(); i++){
            gc.strokeLine(0, offset, canvas.getWidth(), offset);
            offset += yOffset;
        }

        if(color) colorGrid(canvas, grid);
    }

    /**
     * Color the canvas according to the grid.
     * @param canvas
     * @param grid
     */
    private void colorGrid(Canvas canvas, Grid grid){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        /* Get the offsets */
        double xOffset = canvas.getWidth() / grid.getXDimension();
        double yOffset = canvas.getHeight() / grid.getYDimension();

        /* Color each box */
        double currX = 0;
        double currY = 0;
        for(int x = 0; x < grid.getXDimension(); x++){
            currY = 0;
            for(int y = 0; y < grid.getYDimension(); y++){
                Tile curr = grid.getTileAt(x, y);
                //color the tile according to what it is
                gc.setFill(baseColor);
                if(dShort != null && canvas == canvas_d && dShort.getPath().contains(curr)) gc.setFill(pathColor);
                if(aShort != null && canvas == canvas_a && aShort.getPath().contains(curr)) gc.setFill(pathColor);
                if(curr.isTarget) gc.setFill(targetColor);
                if(!curr.isPassable) gc.setFill(obstacleColor);
                if(grid.getSource() == curr) gc.setFill(sourceColor);
                gc.fillRect(currX, currY, xOffset, yOffset);
                currY += yOffset;
            }
            currX += xOffset;
        }
    }
}
