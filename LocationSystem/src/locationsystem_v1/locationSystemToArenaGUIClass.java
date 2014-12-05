/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package locationsystem_v1;

import LocationSystem.locationSystemListener;
import java.util.LinkedList;
import lunarenagui.autonomousSystemListener;


public class locationSystemToArenaGUIClass implements locationSystemListener {
    
    private autonomousSystemListener lunArenaGUIListener;

    public locationSystemToArenaGUIClass(autonomousSystemListener l){
        this.lunArenaGUIListener = l;
    }
    
    @Override
    public void updateXYPositionOfLunabotInCentimeters(
            int xPositionInCentimeters, 
            int yPositionInCentimeters, 
            float headingAngleInRadians) {
        
        LinkedList obst = new LinkedList();
        LinkedList path = new LinkedList();
        
        short [] s1 = new short[2];
        s1[0] = 471;
        s1[1] = 194;
        
        path.add(s1);
        
        
        
        this.lunArenaGUIListener.updateDataFromAutonomousSystem((short)xPositionInCentimeters, 
                (short)yPositionInCentimeters, 
                obst, 
                path, 
                headingAngleInRadians, 
                "Testing of Autonomous System");
    }
    
}
