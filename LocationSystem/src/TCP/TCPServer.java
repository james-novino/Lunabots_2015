/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;



/**
 *
 * @author Owner
 */
public class TCPServer  {
    
    private TCPServerReceiver rxServ;
    private TCPServerTransmitter txServ;

    public TCPServer(int rxPort, int txPort){
        this.rxServ = new TCPServerReceiver(rxPort);
        this.txServ = new TCPServerTransmitter(txPort);
    }
    
    public int getNumReceivedData(){
        return this.rxServ.numReceivedElements();
    }
    
    public Object getNextReceivedData(){
        return this.rxServ.getNextReceivedObject();
    }
    
    public int getNumElementsNeededToSend(){
        return this.txServ.numElementsToSend();
    }
    
    public void addElementToSend(Object o){
        this.txServ.addObjectToSend(o);
    }
    
    
}
