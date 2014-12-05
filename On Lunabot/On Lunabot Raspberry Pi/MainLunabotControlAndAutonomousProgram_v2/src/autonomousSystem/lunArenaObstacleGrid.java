/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autonomousSystem;

import java.util.LinkedList;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

/**
 *
 * @author chalbers2
 */
public class lunArenaObstacleGrid implements TileBasedMap {
    
    // number of X and Y squares on the map
    public final int XDIM = 369;
    public final int YDIM = 194;
    
    // this is the number to times a square must be marked before being set as an obstacle
    public final int OBSTACLE_REVISIT_THRESHOLD = 10;
    
    public final float centimetersPerXGridUnit = 2.0f;
    public final float centimetersPerYGridUnit = 2.0f;
    
    private byte [][] lunArenaObstacleGrid;
    
    private LinkedList allObstacles;
    private LinkedList obstacleGridToSend;
    
    private int radiusOfObstaclesInCM;
    private LinkedList obstacleMarkingLookUpTable;
    
    public final int OBSTACLE_FIELD_MIN_X_DIM_IN_CENTIMETERS = 150;
    public final int OBSTACLE_FIELD_MAX_X_DIM_IN_CENTIMETERS = 444;
    
    public final int OBSTACLE_FIELD_MIN_Y_DIM_IN_CENTIMETERS = 0;
    public final int OBSTACLE_FIELD_MAX_Y_DIM_IN_CENTIMETERS = 388;
    
    
    public lunArenaObstacleGrid(){
        this.lunArenaObstacleGrid = new byte[XDIM][YDIM];
        this.allObstacles = new LinkedList();
        this.obstacleGridToSend = new LinkedList();
        this.setArenaObstacleGridToZero();
        this.radiusOfObstaclesInCM = 10;
    }
    
    
    
    
    /**
     * This Method will clear the arena obstacle grid
     */
    public void setArenaObstacleGridToZero(){
        for (int i = 0; i < this.XDIM; i++){
            for (int j = 0; j < this.YDIM; j++){
                this.lunArenaObstacleGrid[i][j] = (byte)0;
            }
        }
        this.allObstacles.clear();
        this.obstacleGridToSend.clear();
        this.setObstacleGridToSendClearCommand();
    }
    
    
    /**
     * 
     * @param xInd  - X Index of lunArena Obstacle Array To Test for Obstacle
     * @param yInd  - Y Index of lunArena Obstacle Array to test for obstacle
     * @return      - returns true if there is an obstacle, returns false if there is no obstacle
     */
    public boolean isObstacleAtArenaIndex(int xInd, int yInd){
        if (this.lunArenaObstacleGrid[xInd][yInd] >= this.OBSTACLE_REVISIT_THRESHOLD){
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 
     * @param xInd
     * @param yInd 
     */
    private void increaseObstacleAtGridIndex(int xInd, int yInd){
        // This if statement checks to make sure 
        if (xInd >= this.getXIndexOfXValueInCM(this.OBSTACLE_FIELD_MIN_X_DIM_IN_CENTIMETERS) 
                && xInd < this.getXIndexOfXValueInCM(this.OBSTACLE_FIELD_MAX_X_DIM_IN_CENTIMETERS) 
                && yInd >= this.getYIndexOfYValueInCM(this.OBSTACLE_FIELD_MIN_Y_DIM_IN_CENTIMETERS) 
                && yInd < this.getYIndexOfYValueInCM(this.OBSTACLE_FIELD_MAX_Y_DIM_IN_CENTIMETERS) ){
            if (this.lunArenaObstacleGrid[xInd][yInd] < this.OBSTACLE_REVISIT_THRESHOLD){
                this.lunArenaObstacleGrid[xInd][yInd]++;
                if (this.lunArenaObstacleGrid[xInd][yInd] >= this.OBSTACLE_REVISIT_THRESHOLD){
                    short [] newObstacle = new short[4];
                    newObstacle[0] = (short)(Math.round(xInd*this.centimetersPerXGridUnit));
                    newObstacle[1] = (short)(Math.round(yInd*this.centimetersPerYGridUnit));
                    newObstacle[2] = (short)(Math.round(this.centimetersPerXGridUnit));
                    newObstacle[3] = (short)(Math.round(this.centimetersPerYGridUnit));
                    this.allObstacles.add(newObstacle);
                    this.obstacleGridToSend.add(newObstacle);
                }
            }
        }
    }
    
    /**
     * This method will fill the look up table needed for marking obstacles on the arena grid in a circular fashion
     * This method should be called from the constructor and then only from the recordObstacleAtXYInCMWithRadiusInCM method
     */
    private void fillObstacleRadiusLookUpTable(){
        this.obstacleMarkingLookUpTable = null;
        this.obstacleMarkingLookUpTable = new LinkedList();
        int radiusInIndeces = (int)Math.ceil(this.radiusOfObstaclesInCM/2.0);
        for (int x = -radiusInIndeces - 1; x <= radiusInIndeces + 1; x++){
            for (int y = -radiusInIndeces - 1; y <= radiusInIndeces + 1; y++){
                if (((x*x) + (y*y)) <= (radiusInIndeces*radiusInIndeces)){
                    int [] newToAdd = new int[2];
                    newToAdd[0] = x;
                    newToAdd[1] = y;
                    this.obstacleMarkingLookUpTable.add(newToAdd);
                }
            }
        }
    }
    
    /**
     * This method will record an obstacle at a given X, Y coordinate in the arena with a given radius
     * @param xCM - x coordinate of center of obstacle in arena in centimeters
     * @param yCM - y coordinate of center of obstacle in arena in centimeters
     * @param radiusCM - radius of the obstacle to be marked in the arena in centimeters
     */
    public void recordObstacleAtXYInCMWithRadiusInCM(int xCM, int yCM, int radiusCM){
        if (this.radiusOfObstaclesInCM != radiusCM || this.obstacleMarkingLookUpTable == null){
            if (radiusCM <= 2){
                this.radiusOfObstaclesInCM = 3;
            } else {
                this.radiusOfObstaclesInCM = radiusCM;
            }
            this.fillObstacleRadiusLookUpTable();
        }
        // at this point, the look up table is absolutely filled.
        int xCenterIndex = this.getXIndexOfXValueInCM(xCM);
        int yCenterIndex = this.getYIndexOfYValueInCM(yCM);
        
        for (Object nextObj: this.obstacleMarkingLookUpTable){
            int [] nextArray = (int [])nextObj;
            int xCurrentIndex = xCenterIndex + nextArray[0];
            int yCurrentIndex = yCenterIndex + nextArray[1];
            this.increaseObstacleAtGridIndex(xCurrentIndex, yCurrentIndex);
        }
        
    }
    
    /**
     * This method will return the integer index of a given X coordinate in CM
     * @param xCM - X coordinate in CM
     * @return - corresponding X index
     */
    public int getXIndexOfXValueInCM(int xCM){
        return (int)Math.round(xCM / this.centimetersPerXGridUnit);
    }
    
    /**
     * This method will return the integer index of a given Y coordinate in CM
     * @param yCM - Y coordinate in CM
     * @return - corresponding Y index
     */
    public int getYIndexOfYValueInCM(int yCM){
        return (int)Math.round(yCM / this.centimetersPerYGridUnit);
    }
    
    /**
     * This method will return the position in the arena in Centimeters of a given X Index
     * @param xIndex - X Index value
     * @return - X position in arena in centimeters
     */
    public int getXValueInCMOfXIndex(int xIndex){
        return (int)Math.round(xIndex * this.centimetersPerXGridUnit);
    }
    
    /**
     * This method will return the position in the arena in Centimeters of a given Y Index
     * @param yIndex - Y Index Value
     * @return - Y position in arena in centimeters
     */
    public int getYValueInCMOfYIndex(int yIndex){
        return (int)Math.round(yIndex * this.centimetersPerYGridUnit);
    }
    
    
    
    
    
    
    
    
    
    
    
    /**
     * This method will clear the linked list of obstacles to send to the arena GUI
     * and put the clear command into the new linked list.
     * WARNING: This method should only be called from the setArenaObstacleGridToZero() Method.
     */
    private void setObstacleGridToSendClearCommand(){
        this.obstacleGridToSend.clear();
        short [] s = {-1, -1, -1, -1};
        this.obstacleGridToSend.add(s);
    }
    
    /**
     * This method returns the current LinkedList obstacle grid to send with out clearing the linked list
     * WARNING: Do not use this method when sending data to the autonomous system GUI
     * @return 
     */
    public LinkedList getObstacleGridToSendWithoutClear(){
        LinkedList retval = new LinkedList();
        retval.addAll(this.obstacleGridToSend);
        return retval;
    }
    
    /**
     * This method will return the linked list which should be sent to the autonomous system GUI.
     * After this method is called, the linked list will be cleared. Therefore, only the change in the arena grid will be sent
     * @return 
     */
    public LinkedList getObstacleGridToSendWithClear(){
        LinkedList retval = new LinkedList();
        retval.addAll(this.obstacleGridToSend);
        this.obstacleGridToSend.clear();
        return retval;
    }

    @Override
    public int getWidthInTiles() {
        return this.XDIM;
    }

    @Override
    public int getHeightInTiles() {
         return this.YDIM;
    }

    @Override
    public void pathFinderVisited(int i, int i1) {
        // This will execute whenever the path finder visits a given cell
    }

    /**
     * This method from the Slick Interface will determine whether or not a square is blocked
     * @param pfc
     * @param i - X array index
     * @param i1 - Y Array index
     * @return - Returns a boolean - true if path is blocked, false if not blocked
     */
    @Override
    public boolean blocked(PathFindingContext pfc, int i, int i1) {
        if (this.isObstacleAtArenaIndex(i, i1)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public float getCost(PathFindingContext pfc, int i, int i1) {
        return 1.0f;
    }
    
    
    
    
}
