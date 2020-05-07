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

            decreaseKey(queue, next, top, bottom);
            decreaseKey(queue, next, left, right);
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

    private void decreaseKey(PriorityQueue<Tile> queue, Tile next, Tile top, Tile bottom) {
        if(top != null && top.d_distance > next.d_distance && top.isPassable){
            top.d_distance = next.d_distance + 1;
            queue.remove(top);
            queue.add(top);
        }
        if(bottom != null && bottom.d_distance > next.d_distance && bottom.isPassable){
            bottom.d_distance = next.d_distance + 1;
            queue.remove(bottom);
            queue.add(bottom);
        }
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
        Tile tl = g.getTileAt(source.x - 1, source.y - 1);
        Tile tr = g.getTileAt(source.x + 1, source.y - 1);
        Tile bl = g.getTileAt(source.x - 1, source.y + 1);
        Tile br = g.getTileAt(source.x + 1, source.y + 1);

        //return the smallest tile
        ArrayList<Tile> cheat = new ArrayList<>();
        if(top != null) cheat.add(top);
        if(bottom != null) cheat.add(bottom);
        if(left != null) cheat.add(left);
        if(right != null) cheat.add(right);
        if(tl != null) cheat.add(tl);
        if(tr != null) cheat.add(tr);
        if(bl != null) cheat.add(bl);
        if(br != null) cheat.add(br);
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
        if(lastState.isComplete) return lastState; /* If done, do nothing. */

        //If we are in the gradient descent phase of the algorithm
        if(lastState.targetFound){
            //get the most recent value from the path and continue descent
            Tile last = lastState.shortestPath.getPath().get(lastState.shortestPath.getPath().size() - 1);
            lastState.shortestPath.getPath().add(getSmallestNeighbor(g, last, lastState.shortestPath.getPath()));
            if(lastState.shortestPath.getPath().get(lastState.shortestPath.getPath().size() - 1).d_distance == 0){
                //we are done if distance is zero
                lastState.isComplete = true;
            }
            return lastState;
        }

        // we must be in the search phase, continue searching
        Tile lowest = lastState.queue.poll();
        lastState.observed.remove(lowest);
        lastState.closed.add(lowest);

        if(lowest == g.getTarget()){
            lastState.targetFound = true;
            lastState.shortestPath.addTile(lowest);
            return lastState;
        }

        //update each neighbor and add them to observed
        Tile top = g.getTileAt(lowest.x, lowest.y - 1);
        Tile bottom = g.getTileAt(lowest.x, lowest.y + 1);
        Tile left = g.getTileAt(lowest.x - 1, lowest.y);
        Tile right = g.getTileAt(lowest.x + 1, lowest.y);

        Tile tl = g.getTileAt(lowest.x - 1, lowest.y - 1);
        Tile tr = g.getTileAt(lowest.x + 1, lowest.y - 1);
        Tile bl = g.getTileAt(lowest.x - 1, lowest.y + 1);
        Tile br = g.getTileAt(lowest.x + 1, lowest.y + 1);

        if(top != null && lastState.observed.contains(top)) lastState.observed.add(top);
        if(bottom != null && lastState.observed.contains(bottom)) lastState.observed.add(bottom);
        if(left != null && lastState.observed.contains(left)) lastState.observed.add(left);
        if(right != null && lastState.observed.contains(right)) lastState.observed.add(right);
        if(tl != null && lastState.observed.contains(tl)) lastState.observed.add(tl);
        if(tr != null && lastState.observed.contains(tr)) lastState.observed.add(tr);
        if(bl != null && lastState.observed.contains(bl)) lastState.observed.add(bl);
        if(br != null && lastState.observed.contains(br)) lastState.observed.add(br);

        decreaseKey(lastState.queue, lowest, top, bottom);
        decreaseKey(lastState.queue, lowest, left, right);
        decreaseKey(lastState.queue, lowest, tl, br);
        decreaseKey(lastState.queue, lowest, tr, bl);

        return lastState;
    }
}
