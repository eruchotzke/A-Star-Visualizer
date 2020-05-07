package model;

import java.util.Random;

/**
 * A grid is a rectangular, raster collection of tiles.
 */
public class Grid {
    private Tile[][] grid;
    private Tile target;
    private Tile source;

    /**
     * Construct a new grid.
     * @param xDim The x dimension.
     * @param yDim The y dimension.
     */
    public Grid(int xDim, int yDim){
        grid = new Tile[xDim][yDim];
        Random rand = new Random();

        for(int x = 0; x < xDim; x++){
            for(int y = 0; y < yDim; y++){
                grid[x][y] = new Tile(x, y);
//                if(rand.nextFloat() < 0.25) grid[x][y].isPassable = false;
            }
        }
    }

    public int getXDimension(){
        return grid.length;
    }

    public int getYDimension(){
        return grid[0].length;
    }

    /**
     * Get a tile at a given location. Checks bounds.
     * @param x The x location.
     * @param y The y location.
     * @return
     */
    public Tile getTileAt(int x, int y){
        if(x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) return null;
        return grid[x][y];
    }

    /**
     * Set the target to the supplied tile.
     * @param x
     * @param y
     */
    public void setTarget(int x, int y){
        if(target != null) target.isTarget = false;
        target = getTileAt(x, y);
        target.isTarget = true;
    }

    /**
     * Set the source for the pathfinding.
     * @param x
     * @param y
     */
    public void setSource(int x, int y){
        source = getTileAt(x, y);
    }

    public Tile getSource(){
        return source;
    }

    public Tile getTarget() {
        return target;
    }

    public void copyGrid(Grid other){
        for(int x = 0; x < getXDimension(); x++){
            for(int y = 0; y < getYDimension(); y++){
                getTileAt(x,y).isPassable = other.getTileAt(x,y).isPassable;
            }
        }
    }
}
