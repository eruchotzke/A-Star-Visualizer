package pathfinding;

import model.Grid;
import model.Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * An object representing the current status of a pathfinder incrementally.
 */
public class PathfinderState {

    /* Djikstra Variables */
    public PriorityQueue<Tile> queue;
    public ArrayList<Tile> observed;
    public ArrayList<Tile> closed;

    /* A Star Variables */
    public PriorityQueue<AStarWrapper> open;
    public ArrayList<AStarWrapper> star_closed;
    public ArrayList<AStarWrapper> star_path;

    public Path shortestPath;
    public boolean targetFound = false;
    public boolean isComplete = false;

    public PathfinderState(Grid g, boolean Dijkstra){
        shortestPath = new Path(); /* An empty path to be filled later */


        if(Dijkstra){
            queue = new PriorityQueue<>(100, new Comparator<Tile>() {
                @Override
                public int compare(Tile o1, Tile o2) {
                    return o1.d_distance - o2.d_distance;
                }
            });
            observed = new ArrayList<>();
            closed = new ArrayList<>();

            /* Insert every tile into the queue */
            for(int x = 0; x < g.getXDimension(); x++){
                for(int y = 0; y < g.getYDimension(); y++){
                    Tile curr = g.getTileAt(x, y);
                    curr.d_distance = Integer.MAX_VALUE;
                    if(curr == g.getSource()) curr.d_distance = 0;
                    queue.add(curr);
                }
            }
        } else {
            open = new PriorityQueue<>(100, new Comparator<AStarWrapper>() {
                @Override
                public int compare(AStarWrapper o1, AStarWrapper o2) {
                    return o1.tile.getFDistance(g.getTarget()) - o2.tile.getFDistance(g.getTarget());
                }
            });
            star_closed = new ArrayList<>();
            star_path = new ArrayList<>();

            /* Insert the Root node */
            AStarWrapper source = new AStarWrapper(g.getSource());
            source.tile.g_distance = 0;
            open.add(source);
        }
    }

}
