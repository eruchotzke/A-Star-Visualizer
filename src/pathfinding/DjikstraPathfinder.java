package pathfinding;

import model.Grid;
import model.Tile;

import java.util.ArrayList;
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

        /* Now until the queue is empty or the tile is found, do the algorithm */
        while(!queue.isEmpty()){
            Tile next = queue.poll();
            //if we found the endpoint, we are done
            if(next.isTarget){
                break;
            }
            //update all of the neighbors of this tile (no diagonals)
            Tile top = g.getTileAt(next.x, next.y - 1);
            Tile bottom = g.getTileAt(next.x, next.y + 1);
            Tile left = g.getTileAt(next.x - 1, next.y);
            Tile right = g.getTileAt(next.x + 1, next.y);

            if(top != null && top.d_distance > next.d_distance){
                top.d_distance = next.d_distance + 1;
                queue.remove(top);
                queue.add(top);
            }
            if(bottom != null && bottom.d_distance > next.d_distance){
                bottom.d_distance = next.d_distance + 1;
                queue.remove(bottom);
                queue.add(bottom);
            }
            if(left != null && left.d_distance > next.d_distance) {
                left.d_distance = next.d_distance + 1;
                queue.remove(left);
                queue.add(left);
            }
            if(right != null && right.d_distance > next.d_distance){
                right.d_distance = next.d_distance + 1;
                queue.remove(right);
                queue.add(right);
            }
        }

        /* Now every necessary node has a distance value. Gradient Descent time */
        Path shortestPath = new Path();
        Tile curr = g.getTarget();
        while(curr != g.getSource()){
            //put curr into a list
            shortestPath.addTile(curr);
            //now pick the neighbor with the smallest number
            curr = getSmallestNeighbor(g, curr, shortestPath.getPath());
        }
        shortestPath.addTile(curr);

        return shortestPath;
    }

    /**
     * A helper method to get the smallest neighbor during
     * gradient descent.
     * @param source
     * @return
     */
    private Tile getSmallestNeighbor(Grid g, Tile source, ArrayList<Tile> closed){
        Tile top = g.getTileAt(source.x, source.y - 1);
        Tile bottom = g.getTileAt(source.x, source.y + 1);
        Tile left = g.getTileAt(source.x - 1, source.y);
        Tile right = g.getTileAt(source.x + 1, source.y);

        //return the smallest tile
        ArrayList<Tile> cheat = new ArrayList<>();
        if(top != null) cheat.add(top);
        if(bottom != null) cheat.add(bottom);
        if(left != null) cheat.add(left);
        if(right != null) cheat.add(right);
        cheat.sort(new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                return o1.d_distance - o2.d_distance;
            }
        });

        for(int i = 0; i < cheat.size(); i++){
            if(!closed.contains(cheat.get(i))) return cheat.get(i);
        }

        return null;
    }

    @Override
    public PathfinderState incrementShortestPath(Grid g, PathfinderState lastState) {
        return null;
    }
}
