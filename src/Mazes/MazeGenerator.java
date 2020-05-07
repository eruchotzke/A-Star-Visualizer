package Mazes;

import model.Grid;
import model.Tile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MazeGenerator {

    private static final double CHANCE_OF_CARVE_NORTH = 0.25;

    /**
     * Generate a maze using the sidewinder algorithm.
     * @param g The grid to generate a sidewinder maze on.
     */
    public static void generateSidewinderMaze(Grid g){
        ArrayList<Tile> run = new ArrayList<>();

        //first we need to "wallify" the canvas
        for(int x = 0; x < g.getXDimension(); x++){
            for(int y = 0; y < g.getYDimension(); y++){
                if(y % 2 == 0){
                    g.getTileAt(x, y).isPassable = true;
                } else {
                    g.getTileAt(x, y).isPassable = false;
                }
            }
        }

        //the top row stays empty, but each row under must run the algorithm
        Random rand = new Random();
        for(int row = 2; row < g.getYDimension(); row += 2){
            int column = 0;
            while(column < g.getXDimension()){
                run.add(g.getTileAt(column, row));
                if(rand.nextFloat() < CHANCE_OF_CARVE_NORTH){
                    //if we need to carve up, carve up
                    Tile random = run.get(rand.nextInt(run.size()));
                    g.getTileAt(column, row - 1).isPassable = true;
                    run.clear();
                    if(column < g.getXDimension() - 1){
                        g.getTileAt(column + 1, row).isPassable = false;
                        column += 1;
                    }
                }
                column += 1;
            }
        }
    }

    /**
     * Generate a maze on the supplied grid using a
     * modified prims algorithm. Greedy.
     * @param g The grid to generate a maze on.
     */
    public static void generatePrimsMaze(Grid g){
        boolean[][] map = generateMaze(g.getXDimension(), g.getYDimension());

        for(int x = 0; x < g.getXDimension(); x++){
            for(int y = 0; y < g.getYDimension(); y++){
                g.getTileAt(x, y).isPassable = map[x][y];
            }
        }
    }

    private static boolean[][] generateMaze( final int width, final int height ){
        boolean[][] map = new boolean[width][height];

        final LinkedList<int[]> frontiers = new LinkedList<>();
        final Random random = new Random();
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        frontiers.add(new int[]{x,y,x,y});

        while ( !frontiers.isEmpty() ){
            final int[] f = frontiers.remove( random.nextInt( frontiers.size() ) );
            x = f[2];
            y = f[3];
            if ( map[x][y] == false )
            {
                map[f[0]][f[1]] = map[x][y] = true;
                if ( x >= 2 && map[x-2][y] == false )
                    frontiers.add( new int[]{x-1,y,x-2,y} );
                if ( y >= 2 && map[x][y-2] == false )
                    frontiers.add( new int[]{x,y-1,x,y-2} );
                if ( x < width-2 && map[x+2][y] == false )
                    frontiers.add( new int[]{x+1,y,x+2,y} );
                if ( y < height-2 && map[x][y+2] == false )
                    frontiers.add( new int[]{x,y+1,x,y+2} );
            }
        }

        return map;
    }
}
