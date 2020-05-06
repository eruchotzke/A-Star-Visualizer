package pathfinding;

import model.Tile;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A path is a series of tiles which create a continuous
 * path from a start node to a finish node.
 */
public class Path {

    private ArrayList<Tile> path;
    private int current;

    public Path(){
        path = new ArrayList<>();
        current = 0;
    }

    /**
     * Add a tile to the path if it is not already in the path.
     * @param t
     */
    public void addTile(Tile t){
        if(!path.contains(t)) path.add(t);
    }

    public int getLength(){
        return path.size();
    }

    public ArrayList<Tile> getPath(){
        return path;
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

    @Override
    public String toString(){
        String ret = "[Path] {";
        for(Tile t : path){
            ret += "(" + t.x + "," + t.y + "),";
        }
        return ret + "}";
    }
}
