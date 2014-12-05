/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

import TCP.DataPackage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
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
public class TCPServerTransmitter  implements Runnable {

    Thread thread;
    private LinkedList dataToTransmit;
    private ServerSocket socket;

    public TCPServerTransmitter(int port) {
        thread = new Thread(this, "TCP Server port "+port);
        this.dataToTransmit = new LinkedList();
        try {
            socket = new ServerSocket(port);
            thread.start();
        } catch (Exception e) {
            System.err.println("Exception in TCP server");
        }
        System.out.println("TCP Server Transmitter running on port " + port);

    }
    
    public int numElementsToSend(){
        return this.dataToTransmit.size();
    }
    
    public void addObjectToSend(Object o){
        this.dataToTransmit.add(o);
    }
    
    private Object getNextObjectToSend(){
        return this.dataToTransmit.poll();
    }
    
    
    public void run() {
        while (true) {
            Socket connectionSocket;
            try {
                connectionSocket = socket.accept();
                PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
                while (this.numElementsToSend() != 0){
                    out.print(this.getNextObjectToSend());
                }
                out.close();
                
                
                


                //System.out.println(Arrays.toString(packet.data));
            } catch (IOException ex) {
                ex.printStackTrace();
            } 

        }
    }
}

