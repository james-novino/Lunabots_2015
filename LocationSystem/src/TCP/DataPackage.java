/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

import java.io.Serializable;

public class DataPackage implements Serializable{
    
    private static final long serialVersionUID = 42L;//do not change this
    public byte data[];
    public boolean read = false;
    
    public DataPackage(byte data[]){
        this.data = data;
    }
    
}
