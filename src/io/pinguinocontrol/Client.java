package io.pinguinocontrol;
/* This is the client Thread, which sends information to another device, connected to a Pinguino
 * using UDP.
 * Find more about the format of the messages in http://iobridger.wordpress.com
 * */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.util.Log;
 
public class Client extends Thread implements Runnable {
        
        private DatagramSocket socketclient = null;
        private  DatagramPacket packetclient = null;
        InetAddress adr = null;
        public int portclient = 0;
        public String ipString;
        public static byte[] buf;
        
        @Override
        public void run() {
        	 	super.run();     	
        	   try {
                       
                        adr = InetAddress.getByName(ipString);
                        /* Create new UDP-Socket */
                         socketclient = new DatagramSocket(); 
                                                                
                         /* Create UDP-packet with
                          * data & destination(url+port) */
                        packetclient = new DatagramPacket(buf, buf.length, adr, portclient);
                                                  
                }
                catch (Exception e) {
                        Log.e("UDP", "client: Error", e);
                }
        	
                
        }
        public Client(int port, String ip, byte[] buffer){
        	
        	this.portclient = port;
        	this.ipString = ip;
        	Client.buf = buffer;
        	
        	
        }
        
        public void send(byte[] message){
        	    	
        	try {
       		 	/* Create UDP-packet with*/
        		packetclient.setData(message);
        		Log.d("Client","Enviando..." + message[0] + "," + message[1] + "," +  message[2]);
               /* Send out the packet */
				socketclient.send(packetclient);
				
			} catch (IOException e) {
				
				Log.d("client","Error when sending packet:" + e);
			}
           
        	
        }
        

  
        public synchronized void stopThread(Client theThread){
        	
    	   if (theThread != null)
    	    {
    	        theThread = null;
    	        
    	    }
    	   socketclient.close();
        }
}