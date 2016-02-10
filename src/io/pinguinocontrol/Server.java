package io.pinguinocontrol;
/* This is the Server Thread, which sends information to another device, connected to a Pinguino
 * using UDP.
 * Find more about the format of the messages in http://iobridger.wordpress.com
 * */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
 
public class Server extends Thread implements Runnable {
        
        private DatagramSocket socketServer = null;
        private  DatagramPacket packetServer = null;
        InetAddress adr = null;
        public int portServer = 0;
        public String ipString;
        public static byte[] buf;
        
        @Override
        public void run() {
        	 	super.run();     	
        	   try {
                       
                        adr = InetAddress.getByName(ipString);
                        /* Create new UDP-Socket */
                         socketServer = new DatagramSocket(); 
                                                                
                         /* Create UDP-packet with
                          * data & destination(url+port) */
                        packetServer = new DatagramPacket(buf, buf.length, adr, portServer);
                                                  
                }
                catch (Exception e) {
                        //Log.e("UDP", "Server: Error", e);
                }
        	
                
        }
        public Server(int port, String ip, byte[] buffer){
        	
        	this.portServer = port;
        	this.ipString = ip;
        	Server.buf = buffer;
        	
        	
        }
        
        public void send(byte[] message){
        	    	
        	try {
       		 	/* Create UDP-packet with*/
        		packetServer.setData(message);
              
               /* Send out the packet */
				socketServer.send(packetServer);
				
			} catch (IOException e) {
				
				//Log.d("Server","Error when sending packet:" + e);
			}
           
        	
        }
        

  
        public synchronized void stopThread(Server theThread){
        	
    	   if (theThread != null)
    	    {
    	        theThread = null;
    	        
    	    }
    	   socketServer.close();
        }
}