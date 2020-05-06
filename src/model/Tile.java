package model;

/**
 * A tile is a single square in the grid being modelled.
 */
public class Tile {

    public int x, y;
    public boolean isPassable;
    public boolean isTarget;

    public int d_distance = -1; /* For use with pathfinding */
    public int a_distance = -1; /* Ditto */

    public Tile(int x, int y){
        this.x = x;
        this.y = y;
        this.isPassable = true;
        this.isTarget = false;
    }

    @Override
    public String toString(){
        return "[Tile] (" + x + ", " + y + ") Dist: " + d_distance;
    }
}
