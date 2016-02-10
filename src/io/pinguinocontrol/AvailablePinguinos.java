/** File:         AvailablePinguinos.java
 * Created:      12 April 2014
 * Last Changed: 12 Arpil 2014
 *  Author:       <A HREF="mailto:[io_robertob@hotmail.com]">[Roberto Becerra]</A>
 *  
 *  AvailabePinguinos is a class of an activity that works in parallel with the layout called
 *  activity_available_pinguinos. In the layout, it is displayed the Pinguino Boards that have been
 *  detected by receiving a broadcast message using UDPs containing the IP address and name 
 *  of a Pinguino board connected to the WiFi LAN. 
 *  
 *   This then offers the option of tapping on the name of this Pinguino in order to launch
 *  activity where the control of the board is accomplishe. 
 *  
 *  History:
 *  $Log: javaCodingStd.html,v $
 *  Revision 1.1.1.1  12/04/2014 15:15:25  roberto
 *  
**/
package io.pinguinocontrol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AvailablePinguinos extends Activity {
	
	/* Variables related to the IP address and name received for the Pinguinos */
	public static int port; 
	public static String ip;
	public static String pinguinoIP;
	public static String boardIP;
	public static String board;
	public static Button Button1;
	
	/** This port will be used  here and in the actual control activity, change here if needed **/
	public static int serverPort = 10101;

	/* A variable for the Server Thread to be used */
	private  volatile TServer serverThread1;
	
	/* The handler communicates with the Thread, receiving data from it, in order to update the
	 * GUI. The uiFlag variable tells us in which fragment of the ViewPager we are, so we only deal with the Views
	 * contained in that fragment. 
	 * TODO create logic to execute findViewById only once when accessing the fragment, as when coming back to the same fragment
	 * the identifier of the View might not be null, but might at the same time not match the id of the current inflate!!! */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  
			  /* Bundle used to pass over the handle in between the main thread and the Server thread */
			  Bundle bundle = msg.getData();
			  
			  /* Obtain the IP address and name of board from UDPs received in the Server */
			  board = bundle.getString("board");
			  boardIP = bundle.getString("IP");
				
			  /* An instance of the button where this information is to be assigned to */
			  Button1 = (Button) findViewById(R.id.button1);
			  
			  /* With the info from the bundle, fill a String to be displayed in the button */
			  pinguinoIP = "Pinguino " + board + ": " + boardIP;			  			  
			  SpannableString spanString = new SpannableString(pinguinoIP);
			  spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, 19, 0);
			  Button1.setText(spanString);
			  Button1.setEnabled(true); 
		  }
};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Launch activity layout */
		setContentView(R.layout.activity_available_pinguinos);
		
		/* Get typeface from resources to set button's font*/
		Typeface face=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"); 
				
		/* Custom typeface, roboto this time*/
		TextView AvailableBoards = (TextView) findViewById(R.id.text1);
		
		Button Button1 = (Button) findViewById(R.id.button1);
		Button Button2 = (Button) findViewById(R.id.button2);
		Button Button3 = (Button) findViewById(R.id.button3);
		Button Button4 = (Button) findViewById(R.id.button4);
		Button Button5 = (Button) findViewById(R.id.button5);	

		AvailableBoards.setTypeface(face);
		
		Button1.setTypeface(face);
		Button2.setTypeface(face);
		Button3.setTypeface(face);
		Button4.setTypeface(face);
		Button5.setTypeface(face);
		
		/* Disable button and its text, enable only when information is received */
		Button1.setText("");
		Button1.setEnabled(false);
	}

	
	@Override
	public void onResume (){
		super.onResume();
		
		/* Initiate the network client thread, 0 in IP address for we don't need it now in the Server*/ 
		serverThread1 = new TServer(serverPort, "0", handler);
		serverThread1.start(); 	
						
		
	}

	@Override
	public void onPause(){
		
		super.onPause();

	
		if(serverThread1!=null){
			Log.e("UDP", "C: Interrupting Server from main thread");
			
			/* Call function to set thread to null and force interrupt by closing Datagram Port*/ 
			serverThread1.closeThread();
			
		}
				
				
	}
	
	public void boardsClick(View view){
		
		Intent intent = new Intent(this, Connect.class);
    	
    	intent.putExtra("IP", boardIP);
    	intent.putExtra("PORT", serverPort);
    	intent.putExtra("ISWIFI", true);
    	
    	if(serverThread1!=null){
			Log.e("UDP", "C: Interrupting Server from main thread");
			
			/* Call function to set thread to null and force interrupt by closing Datagram Port*/ 
			serverThread1.closeThread();
			
		}
    	
    	/* Launch activity */
    	startActivity(intent);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			 
				// Inflate the menu; this adds items to the action bar if it is present.
				getMenuInflater().inflate(R.layout.menu, menu);
				return true;
				
	}
	
	 /* Handle menu item selection*/
	 public boolean onOptionsItemSelected(MenuItem item){
			
		 /* Menu uptions when pressing the context button for menu  */
			switch (item.getItemId()){
			
			case R.id.about:/* Sends application to another layout with information about itself  */				
				Intent intent = new Intent(this, About.class);
				startActivity(intent);				
				return true;
				
			case R.id.exit:	/* Closes Application  */		
				this.finish();
			    Intent intentExit = new Intent(Intent.ACTION_MAIN);
			    intentExit.addCategory(Intent.CATEGORY_HOME);
			    intentExit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(intentExit);
				return true;
				
			case R.id.instructions:	/* Sends to another layout with instructive text  */
				Intent intentInstructions = new Intent(this, Instructions.class);
				startActivity(intentInstructions);			
				return true;
				
			default:/* Do nothing  */
				return super.onOptionsItemSelected(item);
			
			}
			
			
	 }
	
	
	/* This is the Server Thread which receives the stream of data coming from somewhere, using UDP packets,
	 * we receive them in the following format:
	 * "/Pinguino/PIC32 OTG/IP A/byte1 byte2 byte3 byte4"
	 * More information about this format in http://iobridger.wordpress.com */		
	public class TServer  extends Thread implements Runnable {
	        
	        private  Bundle 		bundleTServer;
	        private  DatagramSocket socketC;
	        private  DatagramPacket packet;
	        
	        public int 		portServer = 0;
	        public String 	ipString;
	        
	        Handler handler;
	        Message msg ;
	        
	        /* By magic we know, how much data will be waiting for us */
            byte[] dataInput = new  byte[32];
                        
            /* Strings to fill with the data received */
            String pinguinoString;
            String boardString;
            String parameter1String;
        
	        @SuppressLint("NewApi")
			@Override
	        public void run() {
	        	super.run();
	        	
	        	/* Useful to cycle the thread */
		    	Thread thisThread = Thread.currentThread();
		    	
		    	/* Obtain the message from the handler and create a bundle to put in it */
		    	Message msg = handler.obtainMessage();
				bundleTServer = new Bundle();
				
				  	        	    	
				try {              
	                        
		            /* Create new UDP-Socket */                       
		            socketC = new  DatagramSocket(portServer);		               	
		            
		             /* Prepare a UDP-Packet that can
		              * contain the data we want to receive 	             
		              * Creating packet in which to receive data*/
		             packet = new DatagramPacket(dataInput, dataInput.length);	
		             
		             /* Almost infinite loop, break by closing socket */
		             while(serverThread1 == thisThread) {  
		            	 
		            			            		            	 
		            	/****************************** 
		            	/* Receive the UDP-Packet   
		            	 * this is the core of this class */ 
		            	
		          		socketC.receive(packet);	
		          		/******************************/
		          		
		          		   		  	
		          		/* Once the packet has been received, it is logically processed 
		          		 * to find out if it contains a valid message pattern
		          		 *  */
		          		if(dataInput[0] ==  0x2F){ //  "/" in ASCII   			
		          				          			
		          			pinguinoString = new String(Arrays.copyOfRange(dataInput,1,9), "UTF-8");
		          			
		          			          			
		          			if(pinguinoString.equals("Pinguino")){
		          				
		          				boardString = new String(Arrays.copyOfRange(dataInput,11,20), "UTF-8");
		          				
		          				
		          				if(boardString.equals("PIC32 OTG")){
		          					
		          					parameter1String = new String(Arrays.copyOfRange(dataInput, 22, 26), "UTF-8");
		          					
		          					if(parameter1String.equals("IP A")){
		          						
		          						/* If code is here we have received a valid message pattern
		        		          		 * then the bytes corresponding to the IP address are put in a bundle 
		        		          		 * and then into a message to send over the handle to the main thread. 
		        		          		 *  */
		          						bundleTServer.putString("board", boardString);
		          						bundleTServer.putString("IP", (dataInput[28] & 0xFF )+ "." + (dataInput[29] & 0xFF ) + 
		        		          				"." + (dataInput[30] & 0xFF ) + "." + (dataInput[31] & 0xFF ));
				          				
		          						msg = Message.obtain();
				                 		msg.setData(bundleTServer);
				                 		handler.sendMessage(msg);
				                 		
		          						
		          					}
		          					
		          				}
		          				
		          			}
		          			
		          		}  
		             
		             }
		             
		         socketC.close();
		         
	             }
				
	              catch (Exception e) {
	            	  
	                        Log.e("UDP", "Client: Error ", e);
	                                             	
	        	  }   
				
	                
	        }
	        
	        /* Public constructor */
	        public TServer(int port, String ip, Handler handler){
	        	
	        	this.portServer = port;
	        	this.ipString = ip;
	        	this.handler = handler;
	        }
	     
	        /* This method closes the thread by nullifying its instance */
	        public  void closeThread(){
	        	
	        	serverThread1 = null;
	        	socketC.close();
	        	
	        }
	        
        
	}

}
