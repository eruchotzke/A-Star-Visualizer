package pathfinding;

import model.Tile;

public class AStarWrapper {

    public Tile tile;
    public AStarWrapper previous;

    public AStarWrapper(Tile tile){
        this.tile = tile;
    }

    @Override
    public boolean equals(Object other){
        return ((AStarWrapper)(other)).tile.equals(tile);
    }
}
