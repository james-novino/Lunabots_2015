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
 * @author chalbers2
 */
public class TCPClientTransmitter {

    private Socket clientSocket;
    private String ip_address;
    private int port_id;

    public TCPClientTransmitter(String ip, int port) {
        ip_address = ip;
        port_id = port;
    }
    
    public boolean checkConnection(){
        Integer b = new Integer(5);
        return sendData(b);
    }
    
    public void setIP(String ip_address){
        this.ip_address = ip_address;
    }
    
    public void setPort(int port_id){
        this.port_id = port_id;
    }
    
    public String getIP(){
        return ip_address;
    }
    
    public int getPort(){
        return port_id;
    }
    
    /* sendData take a byte an an input, serializes it using DataPackage, and
     sends it through a specified port. It returns true if no error has occurred
     
     if UnknownHostException has occured, the ip address could not be 
     connected to
     */
    public boolean sendData(Object o){
        try {
            clientSocket = new Socket(ip_address, port_id);
            OutputStream outputStream = clientSocket.getOutputStream();
            ObjectOutputStream s = new ObjectOutputStream(outputStream);
            s.writeObject(o);
            s.flush();
            s.close();
            clientSocket.close();
        } catch (UnknownHostException e) {
            System.err.println("TCP Client: Unknown Host Exception!");
            return false;
        } catch (IOException e) {
            System.err.println("TCP Client: I/O exception has occured!");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("TCP Client: an exception has occured!");
            return false;
        }

        return true;
    }

}
