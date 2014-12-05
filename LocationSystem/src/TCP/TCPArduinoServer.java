/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Owner
 */
public class TCPArduinoServer implements Runnable {

    Thread thread;
    public DataPackage packet;
    private ServerSocket socket;
    private long timeout = 2000;
    private long starttime;
    private int port;

    public TCPArduinoServer(int port) {
        thread = new Thread(this, "Arduino TCP Server port " + port);
        this.port = port;
        try {
            socket = new ServerSocket(port);
            socket.setSoTimeout(0);
            thread.start();
        } catch (Exception e) {
            System.err.println("Exception in TCP server");
        }
        System.out.println("TCP Server running");

    }

    public void run() {
        while (true) {
            Socket connectionSocket;
            
            try {
                
                try {
                    
                    socket = new ServerSocket(port);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                System.out.println("Accepting Socket");
                
                connectionSocket = socket.accept();

                System.out.println("Accepted Socket Connection");
                starttime = System.currentTimeMillis();
                
                while(starttime !=0){
                //while (System.currentTimeMillis() - starttime < timeout) {

                    InputStream inputStream = connectionSocket.getInputStream();

                    int available = inputStream.available();
                    byte chunk[] = new byte[available];
                    inputStream.read(chunk, 0, available);


                    if (available > 0) {
                        packet = new DataPackage(chunk);
                        packet.read = false;
                        System.out.println(available);
                        int tmp[] = new int[packet.data.length];
                        for (int i = 0; i < tmp.length; i++) {
                            tmp[i] = BitMath.ByteToInteger(packet.data[i]);
                        }
                        System.out.println(Arrays.toString(tmp));
                        starttime = System.currentTimeMillis();
                    }
                    
//                    try {
//                        thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(TCPArduinoServer.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    
                }

                socket.close();
                System.out.println("Lost Connection");
            } catch (IOException ex) {
            }

        }
    }
}
