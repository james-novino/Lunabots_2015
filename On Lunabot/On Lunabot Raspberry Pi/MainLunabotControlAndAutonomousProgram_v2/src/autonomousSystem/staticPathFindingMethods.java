/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;


import java.util.LinkedList;
import lunarenagui.swing_2d_lunarena.Cell;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

/**
 *
 * @author chalbers2
 */
public class staticPathFindingMethods {
    
    
    
    
    /**
     * Call this method to get the computed Path for the lunabot
     * 
     * WARNING: This is the only method which should be called by the autonomous system.
     * 
     * @param robot - pass in the lunabot
     * @param arena - pass in the arena obstacle grid
     * @param sx    - pass in the starting x position as an int
     * @param sy    - 
     * @param ex
     * @param ey
     * @return 
     */
    public static LinkedList getLunabotPathInCentimeters(lunabot robot, lunArenaObstacleGrid arena, 
                                             int xStartInCM, int yStartInCM, int xEndInCM, int yEndInCM ){
        LinkedList retval = new LinkedList();
        
        int [][] bigPath = findPathAStarSlick(robot,  arena, 
                                             arena.getXIndexOfXValueInCM(xStartInCM),
                                             arena.getYIndexOfYValueInCM(yStartInCM),
                                             arena.getXIndexOfXValueInCM(xEndInCM),
                                             arena.getYIndexOfYValueInCM(yEndInCM) );
        
        if (bigPath != null){
            int [][] splitPath = splitPath(arena, bigPath);
            if (splitPath != null){
                splitPath = splitPath(arena, splitPath);
                if (splitPath != null){
                    splitPath = splitPath(arena, splitPath);
                    if (splitPath != null){
                        for (int i = 0; i<splitPath.length; i++){
                            short [] toAdd = new short[2];
                            toAdd[0] = (short)arena.getXValueInCMOfXIndex(splitPath[i][0]);
                            toAdd[1] = (short)arena.getYValueInCMOfYIndex(splitPath[i][1]);
                            if (toAdd[0] >= 0 && toAdd[1] >= 0){
                                retval.add(toAdd);
                            } else {
                                retval.clear();
                                i = splitPath.length + 7;
                            }
                        }
                    }
                }
            }
        }
        
        
        
        return retval;
    }
    
    
    
    /**
     * This Method uses the A-Star implementation from the 
     * 
     * @param robot
     * @param arena
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     * @return 
     */
    private static int[][] findPathAStarSlick(lunabot robot, lunArenaObstacleGrid arena, 
                                             int sx, int sy, int ex, int ey ){
        int maxNum = (arena.XDIM + arena.YDIM)*10;
        AStarPathFinder aStar = new AStarPathFinder(arena, maxNum, true);
        Path aStarResult = aStar.findPath(robot, sx, sy, ex, ey);
        int [][]bigPath1 = null;
        if (aStarResult != null){
            int pathLength = aStarResult.getLength();
            bigPath1 = new int[pathLength][2];
            for (int i = 0; i<pathLength; i++){
                bigPath1[i][0] = aStarResult.getX(i);
                bigPath1[i][1] = aStarResult.getY(i);
            }
        } else {
            bigPath1 = new int[1][2];
            bigPath1[0][0] = -1;
            bigPath1[0][1] = -1;
        }
        
        
        
        return bigPath1;
    }
    
    
    // path splitting with bresenham's line algorithm
    private static int[][] splitPath(lunArenaObstacleGrid arena, int [][]path){
        
        int length = path.length - 1;
        
        int xStart = path[0][0];
        int yStart = path[0][1];
        
        int xEnd   = path[length][0];
        int yEnd   = path[length][1];
        
        if (bresenhamObstacleInPath(xStart,yStart,xEnd,yEnd,arena)){
            int start1 = 0;
            int end1   = length/2;
            int start2 = end1 + 1;
            int end2   = length;
            
            int [][]firstSplit = new int[end1-start1+1][2];
            int [][]secondSplit = new int[end2-start2+1][2];
            System.arraycopy(path, start1, firstSplit, 0, end1-start1+1);
            System.arraycopy(path, start2, secondSplit, 0, end2-start2+1);
            
            int [][] firstPath = splitPath(arena,firstSplit);
            int [][] secondPath = splitPath(arena,secondSplit);
            
            int [][] ret = new int[firstPath.length + secondPath.length][2];
            System.arraycopy(firstPath, 0, ret, 0, firstPath.length);
            System.arraycopy(secondPath,0, ret, firstPath.length, secondPath.length);
            return ret;
            
            //int [][]firstPath = copyOfRange(path,start1,end1);
            
        }else {
            int[][] ret = {{xStart,yStart},{xEnd,yEnd}};
            return ret;
        }
    }
    
    
    private static boolean bresenhamObstacleInPath(int xStart,int yStart,
                                                  int xEnd,  int yEnd, lunArenaObstacleGrid arena){
        LinkedList<Cell> l = bresenham(xStart, yStart, xEnd, yEnd);
        boolean obstacle = false;
        Cell current = null;
        while (obstacle == false && l.size() != 0){
             current = l.pop();
             if (arena.isObstacleAtArenaIndex(current.getXValue(), current.getYValue())){
                 obstacle = true;
             }
        }
        return obstacle;
    }
    
    
    // -------------------------------------------------------------------------
    // Bresenham's line algorithm
    private static LinkedList<Cell> bresenham(double sx, double sy, double ex, double ey) {
        LinkedList<Cell> lineList = new LinkedList<Cell>();
        boolean steep = (Math.abs(ey - sy) > Math.abs(ex - sx));
        if (steep) {
            double dummy = sx;
            sx = sy;
            sy = dummy;
            dummy = ex;
            ex = ey;
            ey = dummy;
        }

        if (sx > ex) {
            double dummy = sx;
            sx = ex;
            ex = dummy;
            dummy = ey;
            ey = sy;
            sy = dummy;
        }

        double dx = ex - sx;
        double dy = Math.abs(ey - sy);

        double error = dx / 2.0;
        int ystep = (sy < ey) ? 1 : -1;
        int y = (int) sy;
        int maxX = (int) ex;

        for (int x = (int) sx; x < maxX; x++) {
            if (steep) {
                //grid[y][x] = 1.0;
                lineList.addLast(new Cell(y, x, 0.0));
            } else {
                //grid[x][y] = 1.0;
                lineList.addLast(new Cell(x, y, 0.0));
            }
            error -= dy;
            if (error < 0) {
                y += ystep;
                error += dx;
            }
        }
        return (lineList);
    }
    
}
