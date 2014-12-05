/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationSystem;


public interface locationSystemListener {
    public void updateXYPositionOfLunabotInCentimeters(int xPositionInCentimeters,
                                                       int yPositionInCentimeters, 
                                                       float headingAngleInRadians);
}
