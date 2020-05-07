package model;

/**
 * A tile is a single square in the grid being modelled.
 */
public class Tile {

    public int x, y;
    public boolean isPassable;
    public boolean isTarget;

    public int d_distance = -1; /* For use with pathfinding */
    public int g_distance = -1; /* Distance from the start node */

    public Tile(int x, int y){
        this.x = x;
        this.y = y;
        this.isPassable = true;
        this.isTarget = false;
    }

    /**
     * A function used to estimate the euclidian distance towards the
     * target node.
     * @param target The target being moved towards.
     * @return The estimated distance to the target.
     */
    public int getHDistance(Tile target){
        int dx = target.x - x;
        int dy = target.y - y;
        return dx * dx + dy * dy;
    }

    /**
     * Get the f-distance for this node.
     * @param target The target being moved towards.
     * @return The heuristic for this node.
     */
    public int getFDistance(Tile target){
        return getHDistance(target) + g_distance;
    }

    @Override
    public String toString(){
        return "[Tile] (" + x + ", " + y + ") Dist: " + d_distance;
    }
}
