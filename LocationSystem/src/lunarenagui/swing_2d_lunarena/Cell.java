/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lunarenagui.swing_2d_lunarena;

/**
 *
 * @author chalbers2
 */
public class Cell implements Comparable {
    
    private int x, y;
    private double value;

    public Cell(int x, int y, double value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
    
    public int getXValue(){
        return this.x;
    }
    
    public int getYValue(){
        return this.y;
    }
    
    public double getScoreValue(){
        return this.value;
    }

    @Override
    public int compareTo(Object o) {
        Cell c = (Cell) o;
        int ret = 0;
        if (value < c.value) {
            ret = -1;
        } else if (value == c.value) {
            ret = 0;
        } else if (value > c.value) {
            ret = 1;
        }
        return (ret);
    }
    
}
