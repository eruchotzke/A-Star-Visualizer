package pathfinding;

import model.Tile;

import java.util.ArrayList;

/**
 * A path is a series of tiles which create a continuous
 * path from a start node to a finish node.
 */
public class Path {

    private ArrayList<Tile> path;
    private int current;

    public Path(Tile start){
        path = new ArrayList<>();
        path.add(start);
        current = 0;
    }

    public int getLength(){
        return path.size();
    }

    /**
     * Traverse this path. Returns the next node in the series.
     * @return
     */
    public Tile traverse(){
        if(current >= getLength()) return null;
        Tile next = path.get(current);
        current += 1;
        return next;
    }
}
