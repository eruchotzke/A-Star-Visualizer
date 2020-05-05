package pathfinding;

import model.Grid;

/**
 * A pathfinder is any class capable of taking in a graph and
 * returning a path from start to finish.
 */
public interface Pathfinder {
    /**
     * Completely generate the shortest path.
     * @param g The grid to generate a path on.
     * @return A path object.
     */
    Path generateShortestPath(Grid g);

    /**
     * Incrementally find the shortest path.
     * @param g The grid being operated on.
     * @param lastState The last state to increment further on.
     * @return A new pathfinder state incrementally progressed from the last.
     */
    PathfinderState incrementShortestPath(Grid g, PathfinderState lastState);
}
