/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

import TCP.DataPackage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chalbers2
 */
public class TCPServerReceiver implements Runnable {

    Thread thread;
    private LinkedList receivedData;
    private ServerSocket socket;

    public TCPServerReceiver(int port) {
        thread = new Thread(this, "TCP Server port "+port);
        this.receivedData = new LinkedList();
        try {
            socket = new ServerSocket(port);
            thread.start();
        } catch (Exception e) {
            System.err.println("Exception in TCP server");
        }
        System.out.println("TCP Server Receiver running on port " + port);

    }
    
    public int numReceivedElements(){
        return this.receivedData.size();
    }
    
    public Object getNextReceivedObject(){
        return this.receivedData.poll();
    }
    
    
    public void run() {
        while (true) {
            Socket connectionSocket;
            try {
                connectionSocket = socket.accept();
                InputStream inputStream = connectionSocket.getInputStream();
                ObjectInputStream oiStream = new ObjectInputStream(inputStream);
                Object next = oiStream.readObject();
                if (next != null){
                    this.receivedData.add(next);
                }
                
                


                //System.out.println(Arrays.toString(packet.data));
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            
        }
    }
}
