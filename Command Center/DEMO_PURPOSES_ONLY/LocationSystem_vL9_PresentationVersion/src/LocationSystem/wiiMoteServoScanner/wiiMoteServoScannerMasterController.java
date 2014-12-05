/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationSystem.wiiMoteServoScanner;

import LocationSystem.beaconACKListener;
import WiiMoteGraphics.wiiMoteIRMonitor;
import wiiMoteArduinoInterface.rocketBrandArduinoUno;
import wiiMoteParticleFilter.singleWiiMoteParticleFilter;

/**
 *
 * @author chalbers2
 */
public class wiiMoteServoScannerMasterController implements beaconACKListener {
    
    private singleWiiMoteParticleFilter particleFilter;
    private rocketBrandArduinoUno rocketBrandArduino;
    private wiiMoteIRMonitor wiiMoteGraphics;
    
    
    
    
    
    public wiiMoteServoScannerMasterController(String serialPort, int baudRate, boolean useWiiGUI){
        this.rocketBrandArduino = new rocketBrandArduinoUno(serialPort, baudRate);
        
        this.particleFilter = new singleWiiMoteParticleFilter();
        
        this.rocketBrandArduino.addListener(particleFilter);
        
        this.rocketBrandArduino.setSingleWiiMoteParticleFilter(particleFilter);
        
        
        if (useWiiGUI){
            this.wiiMoteGraphics = new wiiMoteIRMonitor();
            this.wiiMoteGraphics.setVisible(true);
            this.wiiMoteGraphics.addSingleWiiMoteParticleFilter(particleFilter);
            this.rocketBrandArduino.addListener(this.wiiMoteGraphics);
        }
        
    }
    
    public rocketBrandArduinoUno getRocketBrandArduino(){
        return this.rocketBrandArduino;
    }
    
    public boolean isNorthScanner(){
        return this.rocketBrandArduino.isNorthScanner();
    }
    
    public boolean isSouthScanner(){
        return this.rocketBrandArduino.isSouthScanner();
    }
    
    public boolean isScannerNorthSouthKnown(){
        return this.rocketBrandArduino.isScannerNorthSouthKnown();
    }
    
    public boolean isBeaconLocated(){
        return this.particleFilter.isBeaconLocated();
    }
    
    public float getAngleInDegrees(){
        return this.particleFilter.getAngleInDegrees();
    }
    
    public float getAngleInRadians(){
        return this.particleFilter.getAngleInRadians();
    }
            

    @Override
    public void beaconACKReceived(boolean LEDBeaconOn) {
        
        this.rocketBrandArduino.getDataFromWiiMote(LEDBeaconOn);
    }

    @Override
    public void updateCompassInformation(long headingFromGyro) {
        
    }

    
    
    
}
