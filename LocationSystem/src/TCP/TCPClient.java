/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Owner
 */
public class TCPClient {

    TCPClientTransmitter txClient;
    TCPClientReceiver    rxClient;
    
    public TCPClient (String ip, int rxPort, int txPort){
        this.txClient = new TCPClientTransmitter(ip, txPort);
        this.rxClient = new TCPClientReceiver(ip, rxPort);
    }
    
    public int getNumReceivedElements(){
        return this.rxClient.getNumElementsReceived();
    }
    
    public Object getNextReceivedData(){
        return this.rxClient.getNextDataReceived();
    }
    
    public boolean sendObject(Object o){
        return this.txClient.sendData(o);
    }
    
    

}
