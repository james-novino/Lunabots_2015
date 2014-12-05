/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lunarenagui.swing_2d_lunarena;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import lunarenagui.autonomousSystemListener;


/**
 *
 * @author chalbers2
 */
public class lunArenaSwingGUI extends javax.swing.JFrame implements autonomousSystemListener {
    
    public static final double PI = 3.1415926535898;
    
    // sets the buffer size in pixels around the arena
    // This should always be equal to 30 - this was set based on best dimensions
    // for the Frame
    private final int bufferSizeInPixels = 30;
    
    private final int collectionBinSizeInPix_X = 48;
    private final int collectionBinSizeInPix_Y = 165;
    
    private final int collectionBinUpperLeftCorner_X = this.bufferSizeInPixels;
    private final int collectionBinUpperLeftCorner_Y = 
            this.bufferSizeInPixels + 112;
    
    private final int arenaRectUpperLeftCorner_X = this.bufferSizeInPixels + (short)48;
    private final int arenaRectUpperLeftCorner_Y = this.bufferSizeInPixels;
    
    private final int arenaRectSizeInPix_X = 738;
    private final int arenaRectSizeInPix_Y = 388;
    
    private final int xLocationOfStartObstacleLine = 
            this.arenaRectUpperLeftCorner_X + 150;
    
    private final int xLocationOfObstacleDigLine = 
            this.arenaRectUpperLeftCorner_X + 150 + 294;
    
    private final int yStartLocationOfVerticalLines = this.bufferSizeInPixels;
    private final int yEndLocationOfVerticalLines = 
            this.yStartLocationOfVerticalLines + this.arenaRectSizeInPix_Y;
    
    
    private final int xLocationOfNorthScanner = this.arenaRectUpperLeftCorner_X;
    private final int xLocationOfSouthScanner = this.arenaRectUpperLeftCorner_X;
    
    private final int yLocationOfNorthScanner = 
            this.collectionBinUpperLeftCorner_Y + 7;
    private final int yLocationOfSouthScanner = 
            this.yLocationOfNorthScanner + 150;
    
    
    
    private int lunabotWidthInCentimeters = 75;
    private float lunabotLengthInCentimeters = 150;
    
    private int lunabotXPositionInArenaInCentimeters = 638;
    private int lunabotYPositionInArenaInCentimeters = 100;
    
    private float lunabotHeadingAngleInArenaInRadians = (float)Math.PI/-4.0f;
    
    private LinkedList arenaObstacleGrid;
    private LinkedList robotPath;
    private String autonomousSystemStateString;
    private boolean lunabotPositionKnown;
    
    
    
    
    
    
    private final int PIXELS_PER_METER = 100;
    private int       heightOfArenaDisplayPanelInPixels = 448;
    

    /**
     * Creates new form lunArenaSwingGUI
     */
    public lunArenaSwingGUI() {
        initComponents();
        this.initCustomComponents();
        this.repaint();
    }
    
    /**
     * Initializes the custom components for the GUI
     */
    private void initCustomComponents(){
        this.arenaObstacleGrid = new LinkedList();
        this.robotPath = new LinkedList();
        this.autonomousSystemStateString = "State Unknown";
        this.lunabotPositionKnown = false;
        
        
    }
    
    
    
    /**
     * Called whenever a repaint() is issued
     * @param g - Graphics for the repaint
     */
    private void paintDisplayPanel(Graphics g){
        this.heightOfArenaDisplayPanelInPixels = 
                this.lunArenaDisplayPanel.getHeight();
        
                
        this.drawBackground(g);
        this.drawArena(g);
        this.drawLunabot(g);
        this.drawObstacleFieldGrid(g);
        this.drawPath(g);
        this.drawArenaBox(g);
        this.drawTarget(g);
        this.updateTextFields();
    }
    
    
    private void updateTextFields(){
        this.updateStateText();
        this.updatelunabotPositionText();
        this.updateTargetTextFields();
    }
    
    private void updateTargetTextFields(){
        if (this.robotPath != null){
            if (this.robotPath.size() > 0){
                Object objTarget = null;
                try {
                    objTarget = this.robotPath.getLast();
                } catch (NoSuchElementException ex){
                    
                }
                if (objTarget != null){
                    short [] targetArray = (short [])objTarget;
                    this.xPositionLabelTarget.setText("" + targetArray[0]);
                    this.yPositionLabelTarget.setText("" + targetArray[1]);
                } else {
                    this.xPositionLabelTarget.setText("Unknown");
                    this.yPositionLabelTarget.setText("Unknown");
                }
            } else {
                this.xPositionLabelTarget.setText("Unknown");
                this.yPositionLabelTarget.setText("Unknown");
            }
        } else {
            this.xPositionLabelTarget.setText("Unknown");
            this.yPositionLabelTarget.setText("Unknown");
        }
    }
    
    private void updatelunabotPositionText(){
        if (this.lunabotPositionKnown){
            this.xPositionLabel.setText("" + this.lunabotXPositionInArenaInCentimeters);
            this.yPositionLabel.setText("" + this.lunabotYPositionInArenaInCentimeters);
        } else {
            this.xPositionLabel.setText("Unknown");
            this.yPositionLabel.setText("Unknown");
            
        }
        this.headingLabel.setText("" + (this.lunabotHeadingAngleInArenaInRadians * (180.0f / (float)Math.PI)));

    }
    
    /**
     * This method will update the autonomous state label text field
     */
    private void updateStateText(){
        this.autoSystemStateLabel1.setText(replaceNewLineForHTML(autonomousSystemStateString));
    }
    
    /**
     * This method will draw the target. The target will be the last point in the path list
     * @param g 
     */
    private void drawTarget(Graphics g){
        if (this.robotPath != null){
            if (this.robotPath.size() > 0){
                Object objTarget = null;
                try {
                    objTarget = this.robotPath.getLast();
                } catch (NoSuchElementException ex){
                    
                }
                if (objTarget != null){
                    short [] targetArray = (short [])objTarget;
                    int xTarget = this.getXPositionInPixelsForDrawing((int)targetArray[0]);
                    int yTarget = this.getYPositionInPixelsForDrawing((int)targetArray[1]);
                    int targetHeight = 40;
                    int targetLength = 20;
                    
                    g.setColor(Color.BLACK);
                    g.drawLine(xTarget, yTarget, xTarget, yTarget - targetHeight);
                    
                    int [] x = new int[3];
                    int [] y = new int[3];
                    
                    x[0] = xTarget;
                    x[1] = xTarget;
                    x[2] = xTarget - targetLength;
                    
                    y[0] = yTarget - targetHeight;
                    y[2] = y[0] + 10;
                    y[1] = y[2] + 10;
                    
                    g.setColor(Color.GREEN);
                    g.fillPolygon(x, y, 3);
                    
                }
            }
        }
    }
    
    /**
     * Draws the planned path for the robot
     * @param g Graphics for repaint
     */
    private void drawPath(Graphics g){
        if (this.robotPath != null){
            g.setColor(Color.BLUE);
            
            short [] thisPathArray = new short[2];
            thisPathArray[0] = (short)this.lunabotXPositionInArenaInCentimeters;
            thisPathArray[1] = (short)this.lunabotYPositionInArenaInCentimeters;
            for (Object nextPathObj: this.robotPath){
                short [] nextPathArray = (short []) nextPathObj;
                int xStart = getXPositionInPixelsForDrawing((int)thisPathArray[0]);
                int yStart = getYPositionInPixelsForDrawing((int)thisPathArray[1]);
                int xEnd   = getXPositionInPixelsForDrawing((int)nextPathArray[0]);
                int yEnd   = getYPositionInPixelsForDrawing((int)nextPathArray[1]);
                thisPathArray[0] = nextPathArray[0];
                thisPathArray[1] = nextPathArray[1];
                // draw the line
                g.drawLine(xStart, yStart, xEnd, yEnd);
            }
            
        }
    }
    
    /**
     * Draws the obstacle field as it is currently known
     * @param g Graphics for repaint
     */
    private void drawObstacleFieldGrid(Graphics g){
        if (this.arenaObstacleGrid != null){
            g.setColor(Color.RED);
            for (Object obstField: this.arenaObstacleGrid){
                short [] obstFieldArray = (short []) obstField;
                int xStart = getXPositionInPixelsForDrawing((int)obstFieldArray[0]);
                int yStart = getYPositionInPixelsForDrawing((int)obstFieldArray[1]);
                int width  = (int)obstFieldArray[2];
                int height = (int)obstFieldArray[3];
                
                // draw command goes here if we want no checking.
                // for testing the autonomous system, we don't want any checking here.
                g.fillRect(xStart, yStart, width, height);
                
                // check to make sure that xStart coordinate is valid
                if (xStart >= this.arenaRectUpperLeftCorner_X && 
                        xStart < (this.arenaRectSizeInPix_X + this.arenaRectUpperLeftCorner_X)){
                    if (yStart >= this.arenaRectUpperLeftCorner_Y && 
                            yStart < (this.arenaRectSizeInPix_Y + this.arenaRectUpperLeftCorner_Y)){
                        if (xStart + width > (this.arenaRectSizeInPix_X + this.arenaRectUpperLeftCorner_X)){
                            // if we go outside of the bounds, only draw within bounds
                            width = (this.arenaRectSizeInPix_X + this.arenaRectUpperLeftCorner_X) - xStart;
                        }
                        if (yStart + height > (this.arenaRectSizeInPix_Y + this.arenaRectUpperLeftCorner_Y)){
                            height = (this.arenaRectSizeInPix_Y + this.arenaRectUpperLeftCorner_Y) - yStart;
                        }
                        if (width > 0 && height > 0){
                            // if we want to do checking, put the draw command here.
                            
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Returns the x argument in pixels required for drawing on the panel
     * @param xInCentimeters - X position in Arena in Centimeters
     * @return - X argument in pixels to plot on the panel
     */
    private int getXPositionInPixelsForDrawing(int xInCentimeters){
        return this.arenaRectUpperLeftCorner_X + xInCentimeters;
    }
    
    /**
     * Returns the y argument in pixels required for drawing on the panel
     * @param yInCentimeters - Y position in Arena in Centimeters
     * @return - Y argument in pixels to plot on the panel
     */
    private int getYPositionInPixelsForDrawing(int yInCentimeters){
        return this.heightOfArenaDisplayPanelInPixels - (this.arenaRectUpperLeftCorner_Y + yInCentimeters);
    }
    
    /**
     * Draws the lunabot in the arena
     * @param g 
     */
    private void drawLunabot(Graphics g){
        int xCenterInPanel = getXPositionInPixelsForDrawing(this.lunabotXPositionInArenaInCentimeters);
        int yCenterInPanel = getYPositionInPixelsForDrawing(this.lunabotYPositionInArenaInCentimeters);
        g.setColor(Color.red);
        g.drawLine(xCenterInPanel, yCenterInPanel, 
                xCenterInPanel + (int)Math.round(this.lunabotLengthInCentimeters*Math.cos(this.lunabotHeadingAngleInArenaInRadians)), 
                (yCenterInPanel - (int)Math.round(this.lunabotLengthInCentimeters*Math.sin(this.lunabotHeadingAngleInArenaInRadians))));
        
        
        if (this.lunabotPositionKnown){
            drawAngledRectangle( g, 
                                 xCenterInPanel, 
                                 yCenterInPanel,
                                 (double)this.lunabotLengthInCentimeters/100.0,
                                 (double)this.lunabotWidthInCentimeters/100.0,
                                 100.0,
                                 this.lunabotHeadingAngleInArenaInRadians,
                                 Color.magenta);
        } else {
            drawAngledRectangle( g, 
                                 xCenterInPanel, 
                                 yCenterInPanel,
                                 (double)this.lunabotLengthInCentimeters/100.0,
                                 (double)this.lunabotWidthInCentimeters/100.0,
                                 100.0,
                                 this.lunabotHeadingAngleInArenaInRadians,
                                 Color.darkGray);
        }
    }
    
    /**
     * Draws the collection bin on the lunarena
     * @param g Pass in graphics from the paintComponent method
     */
    private void drawCollectionBin(Graphics g){
        g.setColor(Color.lightGray);
        g.fillRect(collectionBinUpperLeftCorner_X, collectionBinUpperLeftCorner_Y,
                collectionBinSizeInPix_X, collectionBinSizeInPix_Y);
        g.setColor(Color.black);
        g.drawRect(collectionBinUpperLeftCorner_X, collectionBinUpperLeftCorner_Y,
                collectionBinSizeInPix_X, collectionBinSizeInPix_Y);
    }
    
    /**
     * Draws boundary lines on the arena field.
     * @param g Pass in graphics from the paintComponent method
     */
    private void drawBoundaries(Graphics g){
        this.drawStartObstacleBoundary(g);
        this.drawObstacleDigBoundary(g);
        this.drawStartingBoxes(g);
    }
    
    /**
     * Draws the starting boxes in the arena
     * @param g Graphics for repaint
     */
    private void drawStartingBoxes(Graphics g){
        g.setColor(Color.yellow);
        g.drawLine(this.arenaRectUpperLeftCorner_X, 
                (this.arenaRectSizeInPix_Y/2) + this.arenaRectUpperLeftCorner_Y, 
                this.xLocationOfStartObstacleLine, 
                (this.arenaRectSizeInPix_Y/2) + this.arenaRectUpperLeftCorner_Y);
    }
    
    /**
     * Draws the boundary line between the starting area and the obstacle area.
     * @param g Pass in graphics from the paintComponent method
     */
    private void drawObstacleDigBoundary(Graphics g){
        // this draws the boundary between the start and obstacle areas
        g.setColor(Color.yellow);
        g.drawLine(this.xLocationOfObstacleDigLine, 
                this.yStartLocationOfVerticalLines,
                this.xLocationOfObstacleDigLine,
                this.yEndLocationOfVerticalLines);
    }
    
    /**
     * Draws the boundary line between the starting area and the obstacle area.
     * @param g Pass in graphics from the paintComponent method
     */
    private void drawStartObstacleBoundary(Graphics g){
        // this draws the boundary between the start and obstacle areas
        g.setColor(Color.yellow);
        g.drawLine(this.xLocationOfStartObstacleLine, 
                this.yStartLocationOfVerticalLines,
                this.xLocationOfStartObstacleLine,
                this.yEndLocationOfVerticalLines);
    }
    
    
    
    /**
     * Draws the LunArena Grid
     * @param g Pass in graphics from the paintComponent method
     */
    private void drawArena(Graphics g){
        
        g.setColor(Color.lightGray);
        g.fillRect(this.bufferSizeInPixels + collectionBinSizeInPix_X, this.bufferSizeInPixels,
                arenaRectSizeInPix_X, arenaRectSizeInPix_Y);
        this.drawBoundaries(g);
        
        this.drawAreaLabelStrings(g);
        
        this.drawCollectionBin(g);
        
        
    }
    
    private void drawArenaBox(Graphics g){
        g.setColor(Color.black);
        g.drawRect(this.bufferSizeInPixels + collectionBinSizeInPix_X, this.bufferSizeInPixels,
                arenaRectSizeInPix_X, arenaRectSizeInPix_Y);
    }
    
    /**
     * Draws the label strings for the arena areas
     * @param g Graphics for repaint
     */
    private void drawAreaLabelStrings(Graphics g){
        int yStringLocation = this.arenaRectUpperLeftCorner_Y - 7;
        
        g.setColor(Color.BLACK);
        g.drawString("Starting Area", this.arenaRectUpperLeftCorner_X + 3,
                yStringLocation);
        g.drawString("Obstacle Area", this.xLocationOfStartObstacleLine + 3,
                yStringLocation);
        g.drawString("Dig Area", this.xLocationOfObstacleDigLine + 3,
                yStringLocation);
    }
    
    /**
     * Draws the background for the jPanel
     * @param g Graphics for repaint
     */
    private void drawBackground(Graphics g){
        g.setColor(new Color(153,153,255));
        g.fillRect(0, 0, 846, 448);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lunArenaDisplayPanel = new javax.swing.JPanel() {
            @Override
            public void paintComponent(Graphics g){
                paintDisplayPanel(g);
            }
        };
        autoSystemStateLabel1 = new javax.swing.JLabel();
        staticStateLabel_DO_NOT_CHANGE = new javax.swing.JLabel();
        staticLunaLabel_DO_NOT_CHANGE = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        xPositionLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        yPositionLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        headingLabel = new javax.swing.JLabel();
        staticLunaLabel_DO_NOT_CHANGE1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        xPositionLabelTarget = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        yPositionLabelTarget = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lunArenaDisplayPanel.setBackground(new java.awt.Color(153, 153, 255));

        org.jdesktop.layout.GroupLayout lunArenaDisplayPanelLayout = new org.jdesktop.layout.GroupLayout(lunArenaDisplayPanel);
        lunArenaDisplayPanel.setLayout(lunArenaDisplayPanelLayout);
        lunArenaDisplayPanelLayout.setHorizontalGroup(
            lunArenaDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 846, Short.MAX_VALUE)
        );
        lunArenaDisplayPanelLayout.setVerticalGroup(
            lunArenaDisplayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 448, Short.MAX_VALUE)
        );

        autoSystemStateLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        autoSystemStateLabel1.setText("State Not Received");
        autoSystemStateLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        staticStateLabel_DO_NOT_CHANGE.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        staticStateLabel_DO_NOT_CHANGE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        staticStateLabel_DO_NOT_CHANGE.setText("Autonomous System State:");

        staticLunaLabel_DO_NOT_CHANGE.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        staticLunaLabel_DO_NOT_CHANGE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        staticLunaLabel_DO_NOT_CHANGE.setText("Lunabot Information:");

        jLabel1.setText("X (cm)");

        xPositionLabel.setText("null");

        jLabel5.setText("Y (cm)");

        yPositionLabel.setText("null");

        jLabel7.setText("Heading (deg)");

        headingLabel.setText("null");

        staticLunaLabel_DO_NOT_CHANGE1.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        staticLunaLabel_DO_NOT_CHANGE1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        staticLunaLabel_DO_NOT_CHANGE1.setText("Target Information:");

        jLabel2.setText("X (cm)");

        xPositionLabelTarget.setText("null");

        jLabel6.setText("Y (cm)");

        yPositionLabelTarget.setText("null");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(autoSystemStateLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(staticStateLabel_DO_NOT_CHANGE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(staticLunaLabel_DO_NOT_CHANGE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                    .add(staticLunaLabel_DO_NOT_CHANGE1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(headingLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(30, 30, 30)
                                .add(yPositionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(30, 30, 30)
                                .add(xPositionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(30, 30, 30)
                                .add(yPositionLabelTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(30, 30, 30)
                                .add(xPositionLabelTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lunArenaDisplayPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(0, 6, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lunArenaDisplayPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(22, 22, 22)
                        .add(staticStateLabel_DO_NOT_CHANGE)
                        .add(18, 18, 18)
                        .add(autoSystemStateLabel1)
                        .add(76, 76, 76)
                        .add(staticLunaLabel_DO_NOT_CHANGE)
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel1)
                            .add(xPositionLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel5)
                            .add(yPositionLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(headingLabel))
                        .add(26, 26, 26)
                        .add(staticLunaLabel_DO_NOT_CHANGE1)
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(xPositionLabelTarget))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel6)
                            .add(yPositionLabelTarget))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(lunArenaSwingGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(lunArenaSwingGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(lunArenaSwingGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(lunArenaSwingGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new lunArenaSwingGUI().setVisible(true);
            }
        });
    }
    
    // -------------------------------------------------------------------------
    // Static Methods
    
    
    /**
     * This method formats a string for HTML for jLabel display with new line characters
     * @param in
     * @return 
     */
    private static String replaceNewLineForHTML(String in){
        String retval = "";
        retval = in.replace("\n", "<br>");
        
        retval = "<html>" + retval + "</html>";
        
        return retval;
    }
    
    // -------------------------------------------------------------------------
    // Bresenham's line algorithm
    public static LinkedList<Cell> bresenham(double sx, double sy, double ex, double ey) {
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
    
    /**
     * This method will draw an angled rectangle as a polygon
     * @param g Graphics for repaint
     * @param xCenter center of rectangle - X
     * @param yCenter center of rectangle - Y
     * @param length  length of rectangle as a double
     * @param width   width of rectangle as a double
     * @param ppm     pixels per meter as a double
     * @param thetaRad  angle of rectangle in radians
     * @param c         color of rectangle
     */
    private static void drawAngledRectangle(Graphics g, 
                             int xCenter, 
                             int yCenter,
                             double length,
                             double width,
                             double ppm,
                             double thetaRad,
                             Color c){
        g.setColor(c);
        
        
        
        
        
        double angle1 = Math.atan(width / length) + thetaRad;
        double angle2 = thetaRad - Math.atan(width / length);
        double angle3 = angle1-PI;
        double angle4 = angle2+PI;
        
        double mag1 = Math.sqrt((length*length/4)+(width*width/4));
        
        int x[] = new int[4];
        int y[] = new int[4];
        
        x[0] = xCenter + (int)Math.round(ppm*mag1*Math.cos(angle1));
        y[0] = yCenter - (int)Math.round(ppm*mag1*Math.sin(angle1));
        
        x[1] = xCenter + (int)Math.round(ppm*mag1*Math.cos(angle2));
        y[1] = yCenter - (int)Math.round(ppm*mag1*Math.sin(angle2));
        
        x[2] = xCenter + (int)Math.round(ppm*mag1*Math.cos(angle3));
        y[2] = yCenter - (int)Math.round(ppm*mag1*Math.sin(angle3));
        
        x[3] = xCenter + (int)Math.round(ppm*mag1*Math.cos(angle4));
        y[3] = yCenter - (int)Math.round(ppm*mag1*Math.sin(angle4));
        
        g.fillPolygon(x, y, 4);
        
        
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel autoSystemStateLabel1;
    private javax.swing.JLabel headingLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel lunArenaDisplayPanel;
    private javax.swing.JLabel staticLunaLabel_DO_NOT_CHANGE;
    private javax.swing.JLabel staticLunaLabel_DO_NOT_CHANGE1;
    private javax.swing.JLabel staticStateLabel_DO_NOT_CHANGE;
    private javax.swing.JLabel xPositionLabel;
    private javax.swing.JLabel xPositionLabelTarget;
    private javax.swing.JLabel yPositionLabel;
    private javax.swing.JLabel yPositionLabelTarget;
    // End of variables declaration//GEN-END:variables

    

    /**
     * Implementation of autonomousSystemListener
     * This method is called whenever new data is received from the autonomous system
     * @param lunabotPositionInCentimeters_X - Lunabot X Position in Centimeters Measured from west (collection bin) wall
     * @param lunabotPositionInCentimeters_Y - Lunabot Y Position in Centimeters Measured from south (bottom) wall
     * @param arenaObstacleGrid - LinkedList four dimensional short arrays as follows: {xPositionOfUpperLeftCornerInArenaInCentimeters, yPositionOfUpperLeftCornerInCentimeters, widthInCentimeters, heightInCentimeters}
     * @param RobotPath - LinkedList containing two dimensional short arrays representing the X and Y locations of the predicted lunabot path in centimeters in the arena coordinate system
     * @param headingAngleInRadians - heading angle in Radians - 0 radians corresponds to heading directly east (dig wall - just like a Cartesian coordinate system).
     * @param currentAutonomousSystemState - String showing the current state of autonomous system FSM - VARIABLE LENGTH
     */
    @Override
    public void updateDataFromAutonomousSystem(short lunabotPositionInCentimeters_X, 
                              short lunabotPositionInCentimeters_Y, 
                              LinkedList arenaObstacleGrid,
                              LinkedList RobotPath, 
                              float headingAngleInRadians,
                              String currentAutonomousSystemState) {
        
        if (lunabotPositionInCentimeters_X > 0){
            this.lunabotPositionKnown = true;
            this.lunabotXPositionInArenaInCentimeters = (int)lunabotPositionInCentimeters_X;
            this.lunabotYPositionInArenaInCentimeters = (int)lunabotPositionInCentimeters_Y;
        } else {
            // lunabot location is not known
            this.lunabotPositionKnown = false;
        }
        
        // check to see if there is anything in the arena obstacle grid
        Object checkObj = arenaObstacleGrid.peek();
        if (checkObj != null){
            short [] checkArray = (short [])checkObj;
            if (checkArray[0] < 0){
                arenaObstacleGrid.pollFirst();
                this.arenaObstacleGrid.clear();
                this.arenaObstacleGrid.addAll(arenaObstacleGrid);
            } else {
                this.arenaObstacleGrid.addAll(arenaObstacleGrid);
            }
        }
        
        // reset the planned robot path
        this.robotPath.clear();
        this.robotPath.addAll(RobotPath);
        
        // reset the heading angle
        this.lunabotHeadingAngleInArenaInRadians = headingAngleInRadians;
        this.autonomousSystemStateString = currentAutonomousSystemState;
        // paint the frame
        this.repaint();
    }

    

    
}
