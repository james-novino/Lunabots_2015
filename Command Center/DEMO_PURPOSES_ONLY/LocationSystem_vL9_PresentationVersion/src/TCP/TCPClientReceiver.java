/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chalbers2
 */
public class TCPClientReceiver implements Runnable {
    
    Thread thread;
    private LinkedList receivedData;
    private Socket socket;
    private ObjectInputStream oiStream;
    private InputStream inStream;
    
    public TCPClientReceiver(String ip, int port){
        /*
        try {
            this.socket = new Socket(ip, port);
            this.inStream = this.socket.getInputStream();
            this.oiStream = new ObjectInputStream(this.inStream);
            thread = new Thread(this);
            this.thread.start();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        * */
        
    }
    
    public int getNumElementsReceived(){
        return this.receivedData.size();
    }
    
    public Object getNextDataReceived(){
        return this.receivedData.poll();
    }

    @Override
    public void run() {
        while(true){
            Object next = null;
            
            try {
                next = this.oiStream.readObject();
                if (next != null){
                    this.receivedData.add(next);
                }
            } catch (IOException ex) {
                Logger.getLogger(TCPClientReceiver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(TCPClientReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
}
