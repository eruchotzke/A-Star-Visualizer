package pathfinding;

import model.Grid;
import model.Tile;

import java.util.Comparator;
import java.util.PriorityQueue;

public class DjikstraPathfinder implements Pathfinder {

    @Override
    public Path generateShortestPath(Grid g) {
        PriorityQueue<Tile> queue = new PriorityQueue<>(100, new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                return o1.d_distance - o2.d_distance;
            }
        });

        /* Insert every tile into the queue */
        for(int x = 0; x < g.getXDimension(); x++){
            for(int y = 0; y < g.getYDimension(); y++){
                Tile curr = g.getTileAt(x, y);
                curr.d_distance = Integer.MAX_VALUE;
                if(curr == g.getSource()) curr.d_distance = 0;
                queue.add(curr);
            }
        }

    }

    @Override
    public PathfinderState incrementShortestPath(Grid g, PathfinderState lastState) {
        return null;
    }
}
