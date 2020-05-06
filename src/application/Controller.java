package application;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import model.*;
import pathfinding.DjikstraPathfinder;
import pathfinding.Path;
import pathfinding.PathfinderState;

import java.net.URL;
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
    Grid dGrid = new Grid(36, 60);
    Grid aGrid = new Grid(36, 60);
    Path dShort;
    Path aShort;
    private PathfinderState state;
    private AnimationTimer dAnimator;

    /*Serialized Resources */
    @FXML
    private Canvas canvas_d;

    @FXML
    private Canvas canvas_a;

    @FXML
    private ChoiceBox clickMode;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas_a.getGraphicsContext2D().setFill(Color.RED);
        canvas_a.getGraphicsContext2D().fillRect(0, 0, canvas_a.getWidth(), canvas_a.getHeight());

        canvas_d.getGraphicsContext2D().setFill(Color.BLUE);
        canvas_d.getGraphicsContext2D().fillRect(0, 0, canvas_d.getWidth(), canvas_d.getHeight());

        clickMode.getItems().add("NONE");
        clickMode.getItems().add("TOGGLE OBSTACLE");
        clickMode.getItems().add("SET SOURCE");
        clickMode.getItems().add("SET TARGET");
        clickMode.setValue(clickMode.getItems().get(0));

        drawGridLines(canvas_d, dGrid, true);
        drawGridLines(canvas_a, aGrid, true);

        dGrid.setSource(0, 0);
        dGrid.setTarget(10, 12);
    }

    public void onDCanvasClick(MouseEvent event){
        Tile selected = getTileAtDCoordinates(event);
        handleMouseClick(selected, dGrid, canvas_d);
    }

    public void onACanvasClick(MouseEvent event){
        Tile selected = getTileAtACoordinates(event);
        handleMouseClick(selected, aGrid, canvas_a);
    }

    private void handleMouseClick(Tile selected, Grid aGrid, Canvas canvas_a) {
        switch(((String)(clickMode.getValue())).toLowerCase()){
            case "none":
                System.out.println(selected);
                break;
            case "toggle obstacle":
                selected.isPassable = !selected.isPassable;
                break;
            case "set source":
                aGrid.setSource(selected.x, selected.y);
                break;
            case "set target":
                aGrid.setTarget(selected.x, selected.y);
                break;
            default:
                System.out.println("Command not found.");
                break;
        }
        drawGridLines(canvas_a, aGrid, true);
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

    public void generateShortestPath(){
        DjikstraPathfinder pathfinder = new DjikstraPathfinder();
        if(dAnimator != null){
            dAnimator.stop();
            dAnimator = null;
        }
        PathfinderState state = new PathfinderState(dGrid);
        while(!state.isComplete){
            state = pathfinder.incrementShortestPath(dGrid, state);
            drawGrid(canvas_d, dGrid, state);
        }
        drawGrid(canvas_d, dGrid, state);
    }

    private void incrementShortestPath(){
        DjikstraPathfinder pathfinder = new DjikstraPathfinder();
        if(state == null) state = new PathfinderState(dGrid);

        state = pathfinder.incrementShortestPath(dGrid, state);
        drawGrid(canvas_d, dGrid, state);

        if(state.isComplete) state = null;
    }

    public void animateShortestPath(){
        dAnimator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                incrementShortestPath();
                if(state == null){
                    dAnimator.stop();
                    dAnimator = null;
                }
            }
        };

        dAnimator.start();
    }

    private void drawGrid(Canvas canvas, Grid grid, PathfinderState state){
        drawGridLines(canvas, grid, false);

        /* Now color tiles according to the state */
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
