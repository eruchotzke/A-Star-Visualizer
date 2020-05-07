package pathfinding;

import model.Grid;
import model.Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarPathfinder implements Pathfinder {

    @Override
    public Path generateShortestPath(Grid g) {
        return null;
    }

    @Override
    public PathfinderState incrementShortestPath(Grid g, PathfinderState lastState) {
        if(lastState.isComplete) return lastState; /* Do nothing if done */

        /* If the target has been found, generate the path. */
        if(lastState.targetFound){
            //get the last tile from the path
            AStarWrapper last = lastState.star_path.get(lastState.star_path.size() - 1);
            //find the next for the path
            lastState.star_path.add(last.previous);

            if(lastState.star_path.get(lastState.star_path.size() - 1).tile == g.getSource()){
                lastState.isComplete = true;
            }

            return lastState;
        }

        /* If we aren't doing descent, do a search */
        AStarWrapper next = lastState.open.poll();
        lastState.star_closed.add(next);

        //if we found the tile being searched for, we are "done" searching
        if(next.tile == g.getTarget()){
            lastState.targetFound = true;
            lastState.star_path.add(next);
            return lastState;
        }

        Tile top = g.getTileAt(next.tile.x, next.tile.y - 1);
        Tile bottom = g.getTileAt(next.tile.x, next.tile.y + 1);
        Tile left = g.getTileAt(next.tile.x - 1, next.tile.y);
        Tile right = g.getTileAt(next.tile.x + 1, next.tile.y);

        Tile tl = g.getTileAt(next.tile.x - 1, next.tile.y - 1);
        Tile tr = g.getTileAt(next.tile.x + 1, next.tile.y - 1);
        Tile bl = g.getTileAt(next.tile.x - 1, next.tile.y + 1);
        Tile br = g.getTileAt(next.tile.x + 1, next.tile.y + 1);

        /* If the neighbor exists and is passable, add it to open */
        if(top != null && top.isPassable){
            AStarWrapper temp = new AStarWrapper(top);
            handleTileReduce(lastState, temp, next);
        }
        if(bottom != null && bottom.isPassable){
            AStarWrapper temp = new AStarWrapper(bottom);
            handleTileReduce(lastState, temp, next);
        }
        if(left != null && left.isPassable){
            AStarWrapper temp = new AStarWrapper(left);
            handleTileReduce(lastState, temp, next);
        }
        if(right != null && right.isPassable){
            AStarWrapper temp = new AStarWrapper(right);
            handleTileReduce(lastState, temp, next);
        }

        if(tl != null && tl.isPassable){
            AStarWrapper temp = new AStarWrapper(tl);
            handleTileReduce(lastState, temp, next);
        }
        if(tr != null && tr.isPassable){
            AStarWrapper temp = new AStarWrapper(tr);
            handleTileReduce(lastState, temp, next);
        }
        if(bl != null && bl.isPassable){
            AStarWrapper temp = new AStarWrapper(bl);
            handleTileReduce(lastState, temp, next);
        }
        if(br != null && br.isPassable){
            AStarWrapper temp = new AStarWrapper(br);
            handleTileReduce(lastState, temp, next);
        }

        return lastState;
    }


    private void handleTileReduce(PathfinderState state, AStarWrapper curr, AStarWrapper next){
        if(!state.star_closed.contains(curr)){ //if the neighbor is not closed
            if(state.open.contains(curr)){
                if(curr.tile.g_distance > next.tile.g_distance + 1){
                    curr.tile.g_distance = next.tile.g_distance + 1;
                    curr.previous = next;
                    state.open.remove(curr);
                    state.open.add(curr);
                } else {
                    //do nothing, this path is shorter.
                }
            } else {
                curr.tile.g_distance = next.tile.g_distance + 1;
                curr.previous = next;
                state.open.add(curr);
            }
        }
    }
}
