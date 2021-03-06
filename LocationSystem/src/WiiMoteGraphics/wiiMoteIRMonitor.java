/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WiiMoteGraphics;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import wiiMoteParticleFilter.IRBlob;
import wiiMoteParticleFilter.wiiMoteListener;
import wiiMoteParticleFilter.particle;
import wiiMoteParticleFilter.singleWiiMoteParticleFilter;


public class wiiMoteIRMonitor extends javax.swing.JFrame implements wiiMoteListener {
    
    private static final boolean DEBUG = false;
    private IRBlob [] blobs;
    int test;
    
    private singleWiiMoteParticleFilter pf;

    /**
     * Creates new form wiiMoteIRMonitor
     */
    public wiiMoteIRMonitor() {
        test = 0;
        initComponents();
        this.updateText();
    }
    
    public void addSingleWiiMoteParticleFilter(singleWiiMoteParticleFilter s){
        this.pf = s;
    }
    
    private void paintDisplayPanel(Graphics g){
        if (DEBUG){
            test++;
            System.out.println("paintDisplayPanel Called! Num:" + test);
        }
        this.drawBackground(g);
        this.drawBlobs(g);
        this.drawParticles(g);
        this.drawMeanParticleLocation(g);
        this.drawBeaconIndicator();
        this.drawAngleIndicator();
        this.drawConfidenceIndicator();
    }
    
    private void drawAngleIndicator(){
        float angle = this.pf.getAngleInDegrees();
        if (angle < -200f || angle >= 200f){
            this.angleIndicator.setText("Angle: Not Yet Reported");
            this.angleIndicator.setForeground(Color.red);
        } else {
            this.angleIndicator.setText("Angle: " + angle + " degrees");
            this.angleIndicator.setForeground(Color.black);
        }
    }
    
    private void drawConfidenceIndicator(){
        float conf = this.pf.getConfidencePercentage();
        this.confidenceIndicator.setText("Confidence: " + conf*100 + "%");
    }
    
    private void drawBeaconIndicator(){
        if (this.pf.isBeaconLocated()){
            this.beaconFoundIndicator.setText("Beacon Found");
            this.beaconFoundIndicator.setForeground(Color.GREEN);
        } else if(this.pf.isServoCurrentlyMoving()){
            this.beaconFoundIndicator.setText("Servo Moving");
            this.beaconFoundIndicator.setForeground(Color.yellow);
        } else {
            this.beaconFoundIndicator.setText("Beacon Not Found");
            this.beaconFoundIndicator.setForeground(Color.red);
        }
    }
    
    private void drawMeanParticleLocation(Graphics g){
        if (this.pf != null){
            g.setColor(Color.PINK);
            int x = Math.round(this.pf.getXMean()/2.0f);
            int y = Math.round(this.pf.getYMean()/2.0f);
            g.fillOval(512-x, 384-y, 10, 10);
        }
    }
    
    private void drawParticles(Graphics g){
        if (this.pf != null){
            particle [] particles = this.pf.getParticles();
            int numParticles = this.pf.getNumParticles();
            for (int i = 0; i<numParticles; i++){
                if (i < this.pf.getNumberOfParticlesToRespawn()){
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.green);
                }
                int x = particles[i].getXPosition()/2;
                int y = particles[i].getYPosition()/2;
                g.fillOval(512-x, 384-y, 4, 4);
            }
        }
    }
    
    
    
    private void drawBlobs(Graphics g){
        if (this.blobs != null){
            for (int i = 0; i<4; i++){
                if (this.blobs[i].isBlobSeen()){
                    int x = this.blobs[i].getXPosition()/2;
                    int y = this.blobs[i].getYPosition()/2;
                    int s = this.blobs[i].getSize()*4;
                    g.setColor(Color.YELLOW);
                    g.fillOval(512-x, 384-y, s, s);
                }
                //System.out.println("For Loop Entered");
            }
        } else {
            return;
        }
    }
    
    private void drawBackground(Graphics g){
        Color bck = new Color(153,153,255);
        g.setColor(bck);
        g.fillRect(0, 0, 512, 384);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayPanel = new javax.swing.JPanel() {
            @Override
            public void paintComponent(Graphics g){
                paintDisplayPanel(g);
            }
        };
        b0_x = new javax.swing.JLabel();
        b0_y = new javax.swing.JLabel();
        b0_s = new javax.swing.JLabel();
        b1_x = new javax.swing.JLabel();
        b1_y = new javax.swing.JLabel();
        b1_s = new javax.swing.JLabel();
        b2_x = new javax.swing.JLabel();
        b2_y = new javax.swing.JLabel();
        b2_s = new javax.swing.JLabel();
        b3_x = new javax.swing.JLabel();
        b3_y = new javax.swing.JLabel();
        b3_s = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        beaconFoundIndicator = new javax.swing.JLabel();
        confidenceIndicator = new javax.swing.JLabel();
        angleIndicator = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        displayPanel.setBackground(new java.awt.Color(153, 153, 255));
        displayPanel.setMaximumSize(new java.awt.Dimension(512, 384));
        displayPanel.setMinimumSize(new java.awt.Dimension(512, 384));

        org.jdesktop.layout.GroupLayout displayPanelLayout = new org.jdesktop.layout.GroupLayout(displayPanel);
        displayPanel.setLayout(displayPanelLayout);
        displayPanelLayout.setHorizontalGroup(
            displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 512, Short.MAX_VALUE)
        );
        displayPanelLayout.setVerticalGroup(
            displayPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 384, Short.MAX_VALUE)
        );

        b0_x.setText("jLabel1");

        b0_y.setText("jLabel1");

        b0_s.setText("jLabel1");

        b1_x.setText("jLabel1");

        b1_y.setText("jLabel1");

        b1_s.setText("jLabel1");

        b2_x.setText("jLabel1");

        b2_y.setText("jLabel1");

        b2_s.setText("jLabel1");

        b3_x.setText("jLabel1");

        b3_y.setText("jLabel1");

        b3_s.setText("jLabel1");

        jLabel13.setText("Blob 0");

        jLabel14.setText("Blob 1");

        jLabel15.setText("Blob 2");

        jLabel16.setText("Blob 3");

        beaconFoundIndicator.setText("Beacon Not Found");

        confidenceIndicator.setText("Confidence:");

        angleIndicator.setText("Angle:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(jLabel14)
                                    .add(jLabel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(b0_x)
                                    .add(b0_y)
                                    .add(b0_s)
                                    .add(b1_x)
                                    .add(b1_y)
                                    .add(b1_s)
                                    .add(b2_x)
                                    .add(b2_y)
                                    .add(b2_s)
                                    .add(b3_x)
                                    .add(b3_y)
                                    .add(b3_s)))
                            .add(beaconFoundIndicator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(displayPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(confidenceIndicator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 244, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(angleIndicator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 244, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(55, 55, 55))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(displayPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(16, 16, 16)
                        .add(b0_x)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(b0_y)
                            .add(jLabel13))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(b0_s)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(b1_x)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(b1_y)
                            .add(jLabel14))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(b1_s)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(b2_x)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(b2_y)
                            .add(jLabel15))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(b2_s)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(b3_x)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(b3_y)
                            .add(jLabel16))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(b3_s)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(beaconFoundIndicator)))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(confidenceIndicator)
                    .add(angleIndicator))
                .add(13, 13, 13))
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
            java.util.logging.Logger.getLogger(wiiMoteIRMonitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(wiiMoteIRMonitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(wiiMoteIRMonitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(wiiMoteIRMonitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new wiiMoteIRMonitor().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel angleIndicator;
    private javax.swing.JLabel b0_s;
    private javax.swing.JLabel b0_x;
    private javax.swing.JLabel b0_y;
    private javax.swing.JLabel b1_s;
    private javax.swing.JLabel b1_x;
    private javax.swing.JLabel b1_y;
    private javax.swing.JLabel b2_s;
    private javax.swing.JLabel b2_x;
    private javax.swing.JLabel b2_y;
    private javax.swing.JLabel b3_s;
    private javax.swing.JLabel b3_x;
    private javax.swing.JLabel b3_y;
    private javax.swing.JLabel beaconFoundIndicator;
    private javax.swing.JLabel confidenceIndicator;
    private javax.swing.JPanel displayPanel;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    // End of variables declaration//GEN-END:variables

    
    
    
    @Override
    public void blobUpdate(IRBlob[] blobs, boolean LED_on) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        this.blobs = blobs;
        this.repaint();
        this.updateText();
    }
    
    private void updateText(){
        if (this.blobs == null){
            this.b0_x.setText("X: null");
            this.b0_y.setText("Y: null");
            this.b0_s.setText("S: null");
            
            this.b1_x.setText("X: null");
            this.b1_y.setText("Y: null");
            this.b1_s.setText("S: null");
            
            this.b2_x.setText("X: null");
            this.b2_y.setText("Y: null");
            this.b2_s.setText("S: null");
            
            this.b3_x.setText("X: null");
            this.b3_y.setText("Y: null");
            this.b3_s.setText("S: null");
        } else {
            if (this.blobs[0].isBlobSeen()){
                this.b0_x.setText("X: " + this.blobs[0].getXPosition());
                this.b0_y.setText("Y: " + this.blobs[0].getYPosition());
                this.b0_s.setText("S: " + this.blobs[0].getSize());
            } else {
                this.b0_x.setText("X: null");
                this.b0_y.setText("Y: null");
                this.b0_s.setText("S: null");
            }
            
            if (this.blobs[1].isBlobSeen()){
                this.b1_x.setText("X: " + this.blobs[1].getXPosition());
                this.b1_y.setText("Y: " + this.blobs[1].getYPosition());
                this.b1_s.setText("S: " + this.blobs[1].getSize());
            } else {
                this.b1_x.setText("X: null");
                this.b1_y.setText("Y: null");
                this.b1_s.setText("S: null");
            }
            
            if (this.blobs[2].isBlobSeen()){
                this.b2_x.setText("X: " + this.blobs[2].getXPosition());
                this.b2_y.setText("Y: " + this.blobs[2].getYPosition());
                this.b2_s.setText("S: " + this.blobs[2].getSize());
            } else {
                this.b2_x.setText("X: null");
                this.b2_y.setText("Y: null");
                this.b2_s.setText("S: null");
            }
            
            if (this.blobs[3].isBlobSeen()){
                this.b3_x.setText("X: " + this.blobs[3].getXPosition());
                this.b3_y.setText("Y: " + this.blobs[3].getYPosition());
                this.b3_s.setText("S: " + this.blobs[3].getSize());
            } else {
                this.b3_x.setText("X: null");
                this.b3_y.setText("Y: null");
                this.b3_s.setText("S: null");
            }
        }
    }
    
    
    
    
}
