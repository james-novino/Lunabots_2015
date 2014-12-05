/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manual_control_gui;

import gamecontroller.GameController;
import java.awt.Color;

/**
 *
 * @author chalbers2
 */
public class manualControlGUI extends javax.swing.JFrame 
implements countdownListener {

    private countdownTimer countdownTimer;
    private GameController gameController;
    
    /**
     * Creates new form manualControlGUI
     */
    public manualControlGUI() {
        initComponents();
    }
    
    public void setJoystickConnected(){
        this.joystickConnectedLabel.setText("Joystick Connected");
        this.joystickConnectedLabel.setForeground(Color.BLACK);
    }
    
    public void setJoystickDisconnected(){
        this.joystickConnectedLabel.setText("Joystick Not Connected");
        this.joystickConnectedLabel.setForeground(Color.red);
        this.joyXPositionLabel.setText("NULL");
        this.joyYPositionLabel.setText("NULL");
        this.joyZPositionLabel.setText("NULL");
        this.robotEnabledLabel.setText("NULL");
        this.leftMotorSpeedLabel.setText("NULL");
        this.rightMotorSpeedLabel.setText("NULL");
        this.collectionMotorSpeedLabel.setText("NULL");
        this.dumpMotorSpeedLabel.setText("NULL");
    }
    
    public GameController getGameController(){
        return this.gameController;
    }
    
    public void setGameController(GameController g){
        this.gameController = g;
    }
    
    public void setCountdownTimer(countdownTimer t){
        this.countdownTimer = t;
        this.countdownTimer.addCountdownListener(this);
    }
    
    public countdownTimer getCountdownTimer(){
        return this.countdownTimer;
    }
    
    public void setJoystickXPosition(float xPosition){
        this.joyXPositionLabel.setText("" + xPosition);
    }
    
    public void setJoystickYPosition(float yPosition){
        this.joyYPositionLabel.setText("" + yPosition);
    }
    
    public void setJoystickZPosition(float zPosition){
        this.joyZPositionLabel.setText("" + zPosition);
    }
    
    public void setCollectionSystemSpeed(float collectionSystemSpeed){
        this.collectionMotorSpeedLabel.setText("" + collectionSystemSpeed);
    }
    
    public void setDumpSystemSpeed(float dumpSystemSpeed){
        this.dumpMotorSpeedLabel.setText("" + dumpSystemSpeed);
    }
    
    public void setMotorSpeeds(float leftMotorSpeed, float rightMotorSpeed){
        this.leftMotorSpeedLabel.setText("" + leftMotorSpeed);
        this.rightMotorSpeedLabel.setText("" + rightMotorSpeed);
    }
    
    public void setRobotEnabled(boolean robotEnabled){
        if (robotEnabled){
            this.robotEnabledLabel.setText("TRUE");
        } else {
            this.robotEnabledLabel.setText("FALSE");
        }
    }
    
    private void startCountdown(){
        if (this.countdownTimer != null){
            this.countdownTimer.startCountdown();
            
            this.runManualControlCheckBox.setSelected(false);
            this.issueStartSignalCheckBox.setSelected(false);
            this.runAutonomousSystemCheckBox.setSelected(false);
            this.issueStopSignalCheckBox.setSelected(false);
            this.issueResetSignalCheckBox.setSelected(false);
            this.setCurrentBooleanValuesToCurrentCheckBoxState();
        }
    }
    
    public void setCheckBoxesToCurrentBooleanValues(){
        // sets the check boxes to the current state of the boolean values in the game controller
        if (this.gameController != null){
        
            this.runManualControlCheckBox.setSelected(this.gameController.getRunManualControlBoolToSend());
            this.issueStartSignalCheckBox.setSelected(this.gameController.getIssueAutonomousStartSignalBoolToSend());
            this.runAutonomousSystemCheckBox.setSelected(this.gameController.getRunAutonomousSystemBoolToSend());
            this.issueStopSignalCheckBox.setSelected(this.gameController.getIssueAutonomousStopSignalBoolToSend());
            this.issueResetSignalCheckBox.setSelected(this.gameController.getIssueAutonomousResetSignalBoolToSend());
        }
    }
    
    public void setCurrentBooleanValuesToCurrentCheckBoxState(){
        // sets the boolean values in the game controller to the currently set checked values in the check boxes
        if (this.gameController != null){
        
            this.gameController.setRunManualControlBooleanValue(this.runManualControlCheckBox.isSelected());
            this.gameController.setIssueAutonomousStartSignalBoolToSend(this.issueStartSignalCheckBox.isSelected());
            this.gameController.setRunAutonomousSystemBoolToSend(this.runAutonomousSystemCheckBox.isSelected());
            this.gameController.setIssueAutonomousStopSignalBoolToSend(this.issueStopSignalCheckBox.isSelected());
            this.gameController.setIssueAutonomousResetSignalBoolToSend(this.issueResetSignalCheckBox.isSelected());
        
        }
    }
    
    private void runManualControl(){
        this.runManualControlCheckBox.setSelected(true);
        this.issueStartSignalCheckBox.setSelected(false);
        this.runAutonomousSystemCheckBox.setSelected(false);
        this.issueStopSignalCheckBox.setSelected(true);
        this.issueResetSignalCheckBox.setSelected(false);
        this.setCurrentBooleanValuesToCurrentCheckBoxState();
    }
    
    private void issueStartSignal(){
        this.runManualControlCheckBox.setSelected(false);
        this.issueStartSignalCheckBox.setSelected(true);
        this.runAutonomousSystemCheckBox.setSelected(true);
        this.issueStopSignalCheckBox.setSelected(false);
        this.issueResetSignalCheckBox.setSelected(false);
        this.setCurrentBooleanValuesToCurrentCheckBoxState();
    }
    
    
    private void issueResetSignal(){
        this.runManualControlCheckBox.setSelected(false);
        this.issueStartSignalCheckBox.setSelected(false);
        this.runAutonomousSystemCheckBox.setSelected(true);
        this.issueStopSignalCheckBox.setSelected(false);
        this.issueResetSignalCheckBox.setSelected(true);
        this.setCurrentBooleanValuesToCurrentCheckBoxState();
    }
    
    private void setAllBooleansToFalse(){
        this.runManualControlCheckBox.setSelected(false);
        this.issueStartSignalCheckBox.setSelected(false);
        this.runAutonomousSystemCheckBox.setSelected(false);
        this.issueStopSignalCheckBox.setSelected(false);
        this.issueResetSignalCheckBox.setSelected(false);
        this.setCurrentBooleanValuesToCurrentCheckBoxState();
    }
    
    public void resetTextLabelsForCheckBoxes(){
        // update the sent / not sent text for all check boxes
        
        if (this.gameController != null){
        
            if (this.runManualControlCheckBox.isSelected() == 
                    this.gameController.getRunManualControlBoolToSend()){
                this.runManualControlLabel.setText("Sent");
            } else {
                this.runManualControlLabel.setText("Not Sent");
            }

            if (this.issueStartSignalCheckBox.isSelected() == 
                    this.gameController.getIssueAutonomousStartSignalBoolToSend()){
                this.issueStartSignalLabel.setText("Sent");
            } else {
                this.issueStartSignalLabel.setText("Not Sent");
            }

            if (this.runAutonomousSystemCheckBox.isSelected() == 
                    this.gameController.getRunAutonomousSystemBoolToSend()){
                this.runAutoSysLabel.setText("Sent");
            } else {
                this.runAutoSysLabel.setText("Not Sent");
            }

            if (this.issueStopSignalCheckBox.isSelected() == 
                    this.gameController.getIssueAutonomousStopSignalBoolToSend()){
                this.issueStopSignalLabel.setText("Sent");
            } else {
                this.issueStopSignalLabel.setText("Not Sent");
            }

            if (this.issueResetSignalCheckBox.isSelected() == 
                    this.gameController.getIssueAutonomousResetSignalBoolToSend()){
                this.issueResetSignalLabel.setText("Sent");
            } else {
                this.issueResetSignalLabel.setText("Not Sent");
            }
        
        }
    }
    
    public float getMainDriveMotorMaxSpeed(){
        float retval = 0.1f;
        try {
            retval = Float.parseFloat(this.mainDriveMotorMaxSpeedTextField.getText());
        } catch (NumberFormatException e){
            retval = 0.1f;
            return retval;
        }
        return retval;
    }
    
    public float getCollectionSystemSpeed(){
        float retval = 1f;
        try {
            retval = Float.parseFloat(this.collectionSystemSpeedTextField.getText());
        } catch (NumberFormatException e){
            retval = 1f;
            return retval;
        }
        return retval;
    }
    
    public float getDumpSystemSpeed(){
        float retval = 1f;
        try {
            retval = Float.parseFloat(this.dumpSystemSpeedTextField.getText());
        } catch (NumberFormatException e){
            retval = 1f;
            return retval;
        }
        return retval;
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topLabel = new javax.swing.JLabel();
        joystickConnectedLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        joyXPositionLabel = new javax.swing.JLabel();
        joyYPositionLabel = new javax.swing.JLabel();
        joyZPositionLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        leftMotorSpeedLabel = new javax.swing.JLabel();
        rightMotorSpeedLabel = new javax.swing.JLabel();
        collectionMotorSpeedLabel = new javax.swing.JLabel();
        dumpMotorSpeedLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        robotEnabledLabel = new javax.swing.JLabel();
        joystickConnectedLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        countdownLabel = new javax.swing.JLabel();
        startCountdownButton = new javax.swing.JButton();
        runManualControlCheckBox = new javax.swing.JCheckBox();
        issueStartSignalCheckBox = new javax.swing.JCheckBox();
        runAutonomousSystemCheckBox = new javax.swing.JCheckBox();
        issueStopSignalCheckBox = new javax.swing.JCheckBox();
        issueResetSignalCheckBox = new javax.swing.JCheckBox();
        runManualControlLabel = new javax.swing.JLabel();
        issueStartSignalLabel = new javax.swing.JLabel();
        runAutoSysLabel = new javax.swing.JLabel();
        issueStopSignalLabel = new javax.swing.JLabel();
        issueResetSignalLabel = new javax.swing.JLabel();
        issueStartWithoutCountdownButton = new javax.swing.JButton();
        abortAutonomousRunManualControl = new javax.swing.JButton();
        transmitCurrentButtonStateButton = new javax.swing.JButton();
        stopAndResetCountdownButton = new javax.swing.JButton();
        resetAutonomousSystemButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        mainDriveMotorMaxSpeedTextField = new javax.swing.JTextField();
        collectionSystemSpeedTextField = new javax.swing.JTextField();
        dumpSystemSpeedTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        topLabel.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLabel.setText("Temple University Lunabotics 2014");
        topLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        joystickConnectedLabel.setText("Joystick Not Connected");

        jLabel2.setText("Joystick X Position:");

        jLabel3.setText("Joystick Y Position:");

        jLabel4.setText("Joystick Z Position:");

        joyXPositionLabel.setText(null);

        joyYPositionLabel.setText(null);

        joyZPositionLabel.setText(null);

        jLabel5.setText("Left Motor Speed:");

        jLabel6.setText("Right Motor Speed:");

        jLabel7.setText("Collection Motor Speed:");

        jLabel8.setText("Dump Motor Speed: ");

        leftMotorSpeedLabel.setText(null);

        rightMotorSpeedLabel.setText(null);

        collectionMotorSpeedLabel.setText(null);

        dumpMotorSpeedLabel.setText(null);

        jLabel9.setText("Robot Enabled:");

        robotEnabledLabel.setText(null);

        joystickConnectedLabel1.setText("Autonomous System");

        jLabel10.setText("Countdown:");

        countdownLabel.setText("1:10");

        startCountdownButton.setText("Start Countdown");
        startCountdownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startCountdownButtonActionPerformed(evt);
            }
        });

        runManualControlCheckBox.setText("Run Manual Control");

        issueStartSignalCheckBox.setText("Issue Start Signal");
        issueStartSignalCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issueStartSignalCheckBoxActionPerformed(evt);
            }
        });

        runAutonomousSystemCheckBox.setText("Run Auto System");

        issueStopSignalCheckBox.setText("Issue Stop Signal");

        issueResetSignalCheckBox.setText("Issue Reset Signal");

        runManualControlLabel.setText("not Sent");

        issueStartSignalLabel.setText("not Sent");

        runAutoSysLabel.setText("not Sent");

        issueStopSignalLabel.setText("not Sent");

        issueResetSignalLabel.setText("not Sent");

        issueStartWithoutCountdownButton.setText("Issue Start Without Countdown");
        issueStartWithoutCountdownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issueStartWithoutCountdownButtonActionPerformed(evt);
            }
        });

        abortAutonomousRunManualControl.setText("Abort Autonomous - Run Manual Control");
        abortAutonomousRunManualControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abortAutonomousRunManualControlActionPerformed(evt);
            }
        });

        transmitCurrentButtonStateButton.setText("Transmit Current Button State");
        transmitCurrentButtonStateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transmitCurrentButtonStateButtonActionPerformed(evt);
            }
        });

        stopAndResetCountdownButton.setText("Stop and Reset Countdown");
        stopAndResetCountdownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopAndResetCountdownButtonActionPerformed(evt);
            }
        });

        resetAutonomousSystemButton.setText("Reset Autonomous System");
        resetAutonomousSystemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetAutonomousSystemButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Drive Motor Max Speed: ");

        jLabel11.setText("Collection System Speed:");

        jLabel12.setText("Dump System Speed:");

        mainDriveMotorMaxSpeedTextField.setText("0.10");

        collectionSystemSpeedTextField.setText("1.0");
        collectionSystemSpeedTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collectionSystemSpeedTextFieldActionPerformed(evt);
            }
        });

        dumpSystemSpeedTextField.setText("1.0");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(topLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(joystickConnectedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel2)
                                        .add(53, 53, 53)
                                        .add(joyXPositionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel3)
                                        .add(53, 53, 53)
                                        .add(joyYPositionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jLabel5)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(leftMotorSpeedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(layout.createSequentialGroup()
                                            .add(jLabel4)
                                            .add(53, 53, 53)
                                            .add(joyZPositionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(rightMotorSpeedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(collectionMotorSpeedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(layout.createSequentialGroup()
                                            .add(jLabel9)
                                            .add(80, 80, 80)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                                .add(robotEnabledLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, dumpMotorSpeedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                                    .add(jLabel7)
                                    .add(jLabel8)
                                    .add(jLabel6))))
                        .add(55, 55, 55)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(issueStartWithoutCountdownButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(startCountdownButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(abortAutonomousRunManualControl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(stopAndResetCountdownButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(layout.createSequentialGroup()
                                                .add(97, 97, 97)
                                                .add(joystickConnectedLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                            .add(layout.createSequentialGroup()
                                                .add(jLabel10)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                .add(countdownLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                        .add(69, 69, 69))
                                    .add(resetAutonomousSystemButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(runManualControlCheckBox)
                                            .add(issueStartSignalCheckBox)
                                            .add(runAutonomousSystemCheckBox)
                                            .add(issueStopSignalCheckBox)
                                            .add(issueResetSignalCheckBox))
                                        .add(41, 41, 41)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(issueResetSignalLabel)
                                            .add(issueStopSignalLabel)
                                            .add(runAutoSysLabel)
                                            .add(issueStartSignalLabel)
                                            .add(runManualControlLabel)))
                                    .add(transmitCurrentButtonStateButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .add(55, 55, 55))
                            .add(layout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel1)
                                    .add(jLabel11)
                                    .add(jLabel12))
                                .add(31, 31, 31)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(dumpSystemSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(collectionSystemSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(mainDriveMotorMaxSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(topLabel)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(runManualControlCheckBox)
                            .add(runManualControlLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(issueStartSignalCheckBox)
                            .add(issueStartSignalLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(runAutonomousSystemCheckBox)
                            .add(runAutoSysLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(issueStopSignalCheckBox)
                            .add(issueStopSignalLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(issueResetSignalCheckBox)
                            .add(issueResetSignalLabel))
                        .add(18, 18, 18)
                        .add(transmitCurrentButtonStateButton))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, stopAndResetCountdownButton)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(joystickConnectedLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                            .add(jLabel2)
                                            .add(joyXPositionLabel))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                            .add(jLabel3)
                                            .add(joyYPositionLabel))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                            .add(jLabel4)
                                            .add(joyZPositionLabel)))
                                    .add(layout.createSequentialGroup()
                                        .add(joystickConnectedLabel1)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                            .add(jLabel10)
                                            .add(countdownLabel))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(startCountdownButton)))
                                .add(24, 24, 24)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(issueStartWithoutCountdownButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel5)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel6)
                                    .add(rightMotorSpeedLabel))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel7)
                                    .add(collectionMotorSpeedLabel))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel8)
                                    .add(dumpMotorSpeedLabel))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel9)
                                    .add(robotEnabledLabel)))
                            .add(layout.createSequentialGroup()
                                .add(abortAutonomousRunManualControl)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(resetAutonomousSystemButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel1)
                                    .add(mainDriveMotorMaxSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel11)
                                    .add(collectionSystemSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel12)
                                    .add(dumpSystemSpeedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(leftMotorSpeedLabel))))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startCountdownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCountdownButtonActionPerformed
        this.startCountdown();
    }//GEN-LAST:event_startCountdownButtonActionPerformed

    private void issueStartSignalCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issueStartSignalCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_issueStartSignalCheckBoxActionPerformed

    private void issueStartWithoutCountdownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issueStartWithoutCountdownButtonActionPerformed
        // TODO add your handling code here:
        this.issueStartSignal();
    }//GEN-LAST:event_issueStartWithoutCountdownButtonActionPerformed

    private void abortAutonomousRunManualControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abortAutonomousRunManualControlActionPerformed
        // TODO add your handling code here:
        this.runManualControl();
    }//GEN-LAST:event_abortAutonomousRunManualControlActionPerformed

    private void transmitCurrentButtonStateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transmitCurrentButtonStateButtonActionPerformed
        // TODO add your handling code here:
        this.setCurrentBooleanValuesToCurrentCheckBoxState();
    }//GEN-LAST:event_transmitCurrentButtonStateButtonActionPerformed

    private void stopAndResetCountdownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopAndResetCountdownButtonActionPerformed
        // TODO add your handling code here:
        if (this.countdownTimer != null){
            this.countdownTimer.stopCountdown();
            this.countdownTimer.resetCountdown();
            this.setAllBooleansToFalse();
        }
    }//GEN-LAST:event_stopAndResetCountdownButtonActionPerformed

    private void resetAutonomousSystemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetAutonomousSystemButtonActionPerformed
        // TODO add your handling code here:
        this.issueResetSignal();
    }//GEN-LAST:event_resetAutonomousSystemButtonActionPerformed

    private void collectionSystemSpeedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collectionSystemSpeedTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_collectionSystemSpeedTextFieldActionPerformed

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
            java.util.logging.Logger.getLogger(manualControlGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(manualControlGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(manualControlGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(manualControlGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new manualControlGUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton abortAutonomousRunManualControl;
    private javax.swing.JLabel collectionMotorSpeedLabel;
    private javax.swing.JTextField collectionSystemSpeedTextField;
    private javax.swing.JLabel countdownLabel;
    private javax.swing.JLabel dumpMotorSpeedLabel;
    private javax.swing.JTextField dumpSystemSpeedTextField;
    private javax.swing.JCheckBox issueResetSignalCheckBox;
    private javax.swing.JLabel issueResetSignalLabel;
    private javax.swing.JCheckBox issueStartSignalCheckBox;
    private javax.swing.JLabel issueStartSignalLabel;
    private javax.swing.JButton issueStartWithoutCountdownButton;
    private javax.swing.JCheckBox issueStopSignalCheckBox;
    private javax.swing.JLabel issueStopSignalLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel joyXPositionLabel;
    private javax.swing.JLabel joyYPositionLabel;
    private javax.swing.JLabel joyZPositionLabel;
    private javax.swing.JLabel joystickConnectedLabel;
    private javax.swing.JLabel joystickConnectedLabel1;
    private javax.swing.JLabel leftMotorSpeedLabel;
    private javax.swing.JTextField mainDriveMotorMaxSpeedTextField;
    private javax.swing.JButton resetAutonomousSystemButton;
    private javax.swing.JLabel rightMotorSpeedLabel;
    private javax.swing.JLabel robotEnabledLabel;
    private javax.swing.JLabel runAutoSysLabel;
    private javax.swing.JCheckBox runAutonomousSystemCheckBox;
    private javax.swing.JCheckBox runManualControlCheckBox;
    private javax.swing.JLabel runManualControlLabel;
    private javax.swing.JButton startCountdownButton;
    private javax.swing.JButton stopAndResetCountdownButton;
    private javax.swing.JLabel topLabel;
    private javax.swing.JButton transmitCurrentButtonStateButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void countdownTimerFinished() {
        
    }

    @Override
    public void updateCountdownInSeconds(int currentCountdownInSeconds, String currentCountdownString) {
        this.countdownLabel.setText(currentCountdownString);
    }

    
}
