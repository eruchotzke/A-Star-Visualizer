package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.*;
import pathfinding.DjikstraPathfinder;
import pathfinding.Path;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    /* Graphics Resources */
    private Color baseColor = Color.WHITE;
    private Color targetColor = Color.RED;
    private Color sourceColor = Color.BLUE;
    private Color obstacleColor = Color.BLACK;
    private Color pathColor = Color.YELLOW;

    /* Model Resources */
    Grid dGrid = new Grid(36, 60);
    Grid aGrid = new Grid(36, 60);

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

        drawGrid(canvas_d, dGrid);
        drawGrid(canvas_a, aGrid);

        dGrid.setSource(0, 0);
        dGrid.setTarget(10, 12);
        generateShortestPath();
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
        drawGrid(canvas_a, aGrid);
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
        Path shortest = pathfinder.generateShortestPath(dGrid);
        System.out.println(shortest);

        //calculate the offsets
        double xOffset = canvas_d.getWidth() / dGrid.getXDimension();
        double yOffset = canvas_d.getHeight() / dGrid.getYDimension();

        canvas_d.getGraphicsContext2D().setFill(pathColor);

        for(Tile t : shortest.getPath()){
            //color this tile
            canvas_d.getGraphicsContext2D().fillRect(t.x * xOffset, t.y * yOffset, (t.x + 1) * xOffset, (t.y + 1) * yOffset);
        }
    }

    /**
     * Draw the grid outline onto a canvas.
     * @param canvas The canvas to draw the grid onto.
     * @param grid The grid to draw onto the canvas.
     */
    private void drawGrid(Canvas canvas, Grid grid){
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

        colorGrid(canvas, grid);
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
