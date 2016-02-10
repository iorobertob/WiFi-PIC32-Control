package io.pinguinocontrol;

/* With a good IP address and valid Port number, coming from the Main Activity, this activity
 * starts two threads handling the reception and sending of UDP packets, it also updates the GUI
 * receive all the click from the multiple views and sends corresponding data via UDP. 
 * The GUI is shown using a View Pager object whose classes and fragments are defined at the bottom of this
 * class. For more information about the format of the messages sent, visit http://iobridger.wordpress.com */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;


public class Connect extends FragmentActivity {

	private  volatile Client clientThread1;
	private  volatile Tserver serverThread1; 
	
	public static int ClientPort = 10111;
	
	/* Communication related variables */
	public static byte 		message[] = new byte[3];
	public static int 		port;
	public static String 	ip;
	public static boolean 	isWiFi;
	public static boolean 	on;
	
	/* Interface's values variables */
	public static int uiFlag = 1;
	public static int pwmValue1 = 0;
	public static int pwmValue2 = 0;
	public static int progress1 = 0;
	public static int seekBarLastProgress1 = 0;
	public static int seekBarLastProgress2 = 0;
	public static int seekBarLastProgress3 = 0;
	
	/* Interface instances */
	public static 	TextView TextA0;
	public static 	TextView TextA1;
	public static 	TextView TextA2;
	public static 	TextView TextA3;
	public static 	TextView TextA4;
	public static 	TextView TextA5;
	public static 	TextView TextA6;
	public static 	TextView TextA7;
	public static 	ToggleButton toggleA0;
	public static 	ToggleButton toggleA1;
	public static 	ToggleButton toggleA2;
	public static 	ToggleButton toggleA3;
	public static 	ToggleButton toggleA4;
	public static 	ToggleButton toggleA5;
	public static 	ToggleButton toggleA6;
	public static 	ToggleButton toggleA7;
	public static 	ToggleButton toggleD0;
	public static 	ToggleButton toggleD1;
	public static 	ToggleButton toggleD2;
	public static 	ToggleButton toggleD3;
	public static 	ToggleButton toggleD4;
	public static 	ToggleButton toggleD5;
	public static 	ToggleButton toggleD6;
	public static 	ToggleButton toggleD7;
	public static 	ToggleButton toggleD8;
	public static 	ToggleButton toggleD9;
	public static 	ToggleButton toggleD10;
	public static 	ToggleButton toggleD11;
	public static 	ToggleButton toggleD12;
	public static 	ToggleButton toggleD13;
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	
	ViewPager mViewPager;
	
	/* The handler communicates with the Thread, receiving data from it, in order to update the
	 * GUI. The uiFlag variable tells us in which fragment of the ViewPager we are, so we only deal with the Views
	 * contained in that fragment. 
	 * TODO create logic to execute findViewById only once when accessing the fragment, as when coming back to the same fragment
	 * the identifier of the View might not be null, but might at the same time not match the id of the current inflate!!! */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  
			  /* Put data into a bundle and get the int array called buffer */
			  Bundle bundle = msg.getData();
			  int[] buf = bundle.getIntArray("buffer");
				
			  /* Depending on the active frame in the ViewPager, and whether the
			   * Views have been created already or not, to not set text again */
			  if(uiFlag == 1){			  
				 
					  TextA0 = (TextView)findViewById(R.id.editText_A0);
					  TextA1 = (TextView)findViewById(R.id.editText_A1);
					  TextA2 = (TextView)findViewById(R.id.editText_A2);
					  TextA3 = (TextView)findViewById(R.id.editText_A3);
					  TextA4 = (TextView)findViewById(R.id.editText_A4);
					  TextA5 = (TextView)findViewById(R.id.editText_A5);
					  TextA6 = (TextView)findViewById(R.id.editText_A6);
					  TextA7 = (TextView)findViewById(R.id.editText_A7);
												  
				  if (TextA0 != null && buf != null){
					
					  TextA0.setText(Integer.toString(buf[0]));
					  TextA1.setText(Integer.toString(buf[1]));
					  TextA2.setText(Integer.toString(buf[2]));
					  TextA3.setText(Integer.toString(buf[3]));
					  
					  if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
						  TextA4.setEnabled(false);
						  TextA5.setEnabled(false);
					  }
					  
					  TextA6.setText(Integer.toString(buf[7]));
					  TextA7.setText(Integer.toString(buf[6]));
				  }else if (TextA0 == null){
					  
					  Log.d("Text", "it is null");
					  
				  }
			  
			  }
			  
			  if(uiFlag == 2){
				  
				  /* Get the state of the pins to display */
				  boolean[] state = bundle.getBooleanArray("booleanBuffer");				 
					  
				   toggleA0 = (ToggleButton) findViewById(R.id.toggle_A0_IO_AP);
				   toggleA1 = (ToggleButton) findViewById(R.id.toggle_A1_IO_AP);
				   toggleA2 = (ToggleButton) findViewById(R.id.toggle_A2_IO_AP);
				   toggleA3 = (ToggleButton) findViewById(R.id.toggle_A3_IO_AP);
				   toggleA4 = (ToggleButton) findViewById(R.id.toggle_A4_IO_AP);
				   toggleA5 = (ToggleButton) findViewById(R.id.toggle_A5_IO_AP);
				   toggleA6 = (ToggleButton) findViewById(R.id.toggle_A6_IO_AP);
				   toggleA7 = (ToggleButton) findViewById(R.id.toggle_A7_IO_AP);
				  
				   
				  if(toggleA0 !=null && state != null){
					  
					  toggleA0.setChecked(state[14]);
					  toggleA1.setChecked(state[15]);
					  toggleA2.setChecked(state[16]);
					  toggleA3.setChecked(state[17]);
					  
					  if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
						  toggleA4.setChecked(state[18]);
						  toggleA5.setChecked(state[19]);
					  }
					  
					  toggleA6.setChecked(state[20]);
					  toggleA7.setChecked(state[21]);
				  }
				  
			  }
			  if(uiFlag == 3){
				  
				  /* Get the state of the pins to display */
				  boolean[] state = bundle.getBooleanArray("booleanBuffer");				  
					  
					   toggleD0 = (ToggleButton) findViewById(R.id.toggle_D0_S_DP);
					   toggleD1 = (ToggleButton) findViewById(R.id.toggle_D1_S_DP);
					   toggleD2 = (ToggleButton) findViewById(R.id.toggle_D2_S_DP);
					   toggleD3 = (ToggleButton) findViewById(R.id.toggle_D3_S_DP);
					   toggleD4 = (ToggleButton) findViewById(R.id.toggle_D4_S_DP);
					   toggleD5 = (ToggleButton) findViewById(R.id.toggle_D5_S_DP);
					   toggleD6 = (ToggleButton) findViewById(R.id.toggle_D6_S_DP);
					   toggleD7 = (ToggleButton) findViewById(R.id.toggle_D7_S_DP);
				  
				  
				  if(toggleD0 != null && state != null){
					  
					  toggleD0.setChecked(state[0]);
					  toggleD1.setChecked(state[1]);
					  toggleD2.setChecked(state[2]);
					  toggleD3.setChecked(state[3]);
					  toggleD4.setChecked(state[4]);
					  toggleD5.setChecked(state[5]);
					  toggleD6.setChecked(state[6]);
					  toggleD7.setChecked(state[7]);
				  }
				  
			  }
			  if(uiFlag == 4){
				  
				  /* Get the state of the pins to display  */
				  boolean[] state = bundle.getBooleanArray("booleanBuffer");				  
				  
					   toggleD8 = (ToggleButton) findViewById(R.id.toggle_D8_S_DP);
					   toggleD9 = (ToggleButton) findViewById(R.id.toggle_D9_S_DP);
					   toggleD10 = (ToggleButton) findViewById(R.id.toggle_D10_S_DP);
					   toggleD11 = (ToggleButton) findViewById(R.id.toggle_D11_S_DP);
					   toggleD12 = (ToggleButton) findViewById(R.id.toggle_D12_S_DP);
					   toggleD13 = (ToggleButton) findViewById(R.id.toggle_D13_S_DP);
				 
				  
				  if(toggleD8 != null && state != null){
					  toggleD8.setChecked(state[8]);
					  toggleD9.setChecked(state[9]);
					  toggleD10.setChecked(state[10]);
					  toggleD11.setChecked(state[11]);
					  toggleD12.setChecked(state[12]);
					  toggleD13.setChecked(state[13]);
				  }
				  
			  }
			  
			  
			
		  }
};
		 
	
	@Override
	protected  void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.connected);
		
		/* Create the adapter that will return a fragment for each of the three
		 primary sections of the app.*/
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		/*Set up the ViewPager with the sections adapter.*/
		mViewPager = (ViewPager) findViewById(R.id.pager);		
		mViewPager.setAdapter(mSectionsPagerAdapter);		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
	        public void onPageSelected(int position) {
				
				/* Flags used to know what Frame of the ViewPager we are in and display corresponding
				 * views */
	        	if (position == 0){
	        		uiFlag = 1;
	        	}
	        	if (position == 1){
	        		uiFlag = 2;
	        	}
	        	if (position == 2){
	        		uiFlag = 3;
	        	}
	        	if (position == 3){
	        		uiFlag = 4;
	        	}
	            if (position == 4) {
	            	uiFlag = 5;
	            	
	            	/* Listen to the progress of the SeekBar's in fragment 5*/
	            	final EditText textopwm1 = (EditText) findViewById(R.id.edit_text_pwm1);
	            	final EditText textopwm2 = (EditText) findViewById(R.id.edit_text_pwm2); 
	            	final EditText textopwm3 = (EditText) findViewById(R.id.edit_text_pwm3); 
	            		
	    	        final  SeekBar seekbar1 = (SeekBar) findViewById(R.id.SeekBar03);
	    	        final  SeekBar seekbar2 = (SeekBar) findViewById(R.id.SeekBar02);
	    	        final  SeekBar seekbar3 = (SeekBar) findViewById(R.id.seekBar1);
	    	            
	    	        seekbar1.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {		    	    	   
					public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser){			    	                                                                       
						
						/* Send values only they are new*/  
						if(progress != seekBarLastProgress1){
							
							/* Arithmetic to change from hundredths to steps of 1/1023  */
							progress1 = (progress*65535)/100;
						
							/* Storing the value of the progress into two bytes to be sent
							 * in a format the microcontroller can handle more easily */
							pwmValue1 = progress1 / 256;
							pwmValue2 = progress1 - (pwmValue1*256);
							
							/* Now putting these two bytes and a protocol related value to send as
							 * one single buffer. Number 224 indicates the type of control we want to perform
							 * on the chip's side */
							message[2] = (byte) pwmValue2;
							message[1] = (byte) pwmValue1;
							message[0] = (byte) 224;
							
							/* Send buffer over UDP */
							clientThread1.send(message);
							
							/* Update the text on the interface */
							textopwm1.setText(String.valueOf(progress));
							
							/* Update the value of the last progress to not do this all over again
							 * if nothing has changed really */
							seekBarLastProgress1 = progress;
						}
	    	    		   
	    	    	   }
	    	    	   public void onStartTrackingTouch(SeekBar seekBar){			    	                                                                       
	    	    		   // TODO Auto-generated method stub
	    	    		   }

	    	    	   public void onStopTrackingTouch(SeekBar seekBar) {
	    	                                                                      
	    	    		   // TODO Auto-generated method stub
	    	          }
	    	       });
	    	        seekbar2.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {		    	    	   
						public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser){			    	                                                                       
							
							/* Send values only they are new*/  
							if(progress != seekBarLastProgress2){
								
								/* Arithmetic to change from hundredths to steps of 1/1023  */
								progress1 = (progress*65535)/100; 
								
								/* Storing the value of the progress into two bytes to be sent
								 * in a format the microcontroller can handle more easily */   
								pwmValue1 = progress1 / 256;
								pwmValue2 = progress1 - (pwmValue1*256);
								
								/* Now putting these two bytes and a protocol related value to send as
								 * one single buffer. Number 225 indicates the type of control we want to perform
								 * on the chip's side */								
								message[2] = (byte) pwmValue2;
								message[1] = (byte) pwmValue1;
								message[0] = (byte) 225;
								
								/* Send buffer over UDP */								
								clientThread1.send(message);
								
								/* Update the text on the interface */
			    	    		textopwm2.setText(String.valueOf(progress));
								
								/* Update the value of the last progress to not do this all over again
								 * if nothing has changed really */	
			    	    		seekBarLastProgress2 = progress;
							}
		    	    		   
		    	    	   }
		    	    	   public void onStartTrackingTouch(SeekBar seekBar){			    	                                                                       
		    	    		   // TODO Auto-generated method stub
		    	    		   }

		    	    	   public void onStopTrackingTouch(SeekBar seekBar) {
		    	                                                                      
		    	    		   // TODO Auto-generated method stub
		    	          }
		    	       });
	    	        seekbar3.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {		    	    	   
						public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser){			    	                                                                       
		    	    		  		    	    		    
							/* Send values only they are new*/ 
							if(progress != seekBarLastProgress3){
								
								/* Arithmetic to change from hundredths to steps of 1/1023  */
								progress1 = (progress*65535)/100;
								
								/* Storing the value of the progress into two bytes to be sent
								 * in a format the microcontroller can handle more easily */  	
								pwmValue1 = progress1 / 256; 
								pwmValue2 = progress1 - (pwmValue1*256);
								  
								/* Now putting these two bytes and a protocol related value to send as
								 * one single buffer. Number 226 indicates the type of control we want to perform
								 * on the chip's side */			
								message[2] = (byte) pwmValue2;
								message[1] = (byte) pwmValue1;
								message[0] = (byte) 226;
								
								/* Send buffer over UDP */								
								clientThread1.send(message);
								
								/* Update the text on the interface */
			    	    		textopwm3.setText(String.valueOf(progress));
								
								/* Update the value of the last progress to not do this all over again
								 * if nothing has changed really */	
			    	    		seekBarLastProgress3 = progress;
							}
		    	    	   }
		    	    	   public void onStartTrackingTouch(SeekBar seekBar){			    	                                                                       
		    	    		   // TODO Auto-generated method stub
		    	    		   }

		    	    	   public void onStopTrackingTouch(SeekBar seekBar) {
		    	                                                                      
		    	    		   // TODO Auto-generated method stub
		    	          }
		    	       });  
	    			

	            }   
	        }
	        @Override
	        public void onPageScrolled(int arg0, float arg1, int arg2) { }
	        @Override
	        public void onPageScrollStateChanged(int arg0) { }
	    });
		
		 
	}

	@Override
	public void onResume (){
		super.onResume();
		
		/* Get a bundle from the intent from previous activity */
		Bundle extras = getIntent().getExtras();
        
		port = extras.getInt("PORT");
		ip = extras.getString("IP");
		isWiFi = extras.getBoolean("ISWIFI");
	
		/* Initiate the network server thread*/ 
		serverThread1 = new Tserver(port, ip, handler);
		serverThread1.start(); 	
		
		/* Initiate the network client thread*/ 
		
		clientThread1 = new Client(ClientPort, ip, message);				
		clientThread1.start();			
		
	} 
	
	@Override
	public void onPause(){
		
		super.onPause();

		uiFlag = 0;
	
		if(serverThread1!=null){
						
			/* Call function to set thread to null and force interrupt by closing Datagram Port*/ 
			serverThread1.closeThread();
			
		}
		
		/* Void the Client Thread to cause and error and stop it in that way */
		if(clientThread1 != null){
			clientThread1 = null;
		}
		
	}
	
	public void onDestroy(){

		super.onDestroy();
		
		uiFlag = 0;
		
		if(serverThread1 != null){
			/* Call function to set thread to null and force interrupt by closing Datagram Port*/ 
			serverThread1.closeThread();
		}
		if(clientThread1 != null){
			/* Void the Client Thread to cause and error and stop it in that way */
			clientThread1 = null;
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.layout.menu, menu);
		return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item){
		
		//Handle item selection
		switch (item.getItemId()){
		
		case R.id.about:	/* Launches another activity with information of the Application  */		
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
			return true;
			
		case R.id.exit:		/* Closes the application  */	
			this.finish();
		    Intent intentExit = new Intent(Intent.ACTION_MAIN);
		    intentExit.addCategory(Intent.CATEGORY_HOME);
		    intentExit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intentExit);
			return true;
			
		case R.id.instructions:	/* Launches another activity with instructions on the usage of the App  */
			Intent intentInstructions = new Intent(this, Instructions.class);
			startActivity(intentInstructions);			
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		
		}
		
		
	}

	
	/* Start of the functions for clicks on the Views, they are generally all the same, fill the byte array 'message' 
	 * with values given by the format (visit http://iobridger.wordpress.com ) and then call the send function
	 * of the client thread to send them over UDP
	 * ioAx , ioDx and ioDLED set whether a Pin is input or output
	 * sDx and sDLed sets the state of the digital output to High or Low.
	 * sAx sets the state of the Pin, when working as output and Digital to High or Low
	 * aAx checkes wethere an Analogue input is to be monitored*/	
	
	public void updatePins(byte IdByte, byte pin, boolean toogle, boolean isAnalogue) {

		message[0] = IdByte;
		message[1] = pin;
		
		
	    if (toogle) {
	    	if (isAnalogue){
	    		message[2] = 2;
	    	}
	    	else{
	    		message[2] = 1;	
	    	}
	    } else {
			message[2] = 0;	
		    }
	    clientThread1.send(message);
		
	}
	
	public void ioA0(View view){
				
		updatePins((byte)244,(byte)1,((ToggleButton) findViewById(R.id.toggle_A0_S_AP)).isChecked(), false);
	    
	}
	
	public void ioA1(View view){
		
		updatePins((byte)244,(byte)2,((ToggleButton) findViewById(R.id.toggle_A1_S_AP)).isChecked(), false);
	}
	
	public void ioA2(View view){
		
		updatePins((byte)244,(byte)3,((ToggleButton) findViewById(R.id.toggle_A2_S_AP)).isChecked(), false);
		
	}
	
	public void ioA3(View view){

		updatePins((byte)244,(byte)4,((ToggleButton) findViewById(R.id.toggle_A3_S_AP)).isChecked(), false);
	}
	
	public void ioA4(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)244,(byte)8,((ToggleButton) findViewById(R.id.toggle_A4_S_AP)).isChecked(), false);
			updatePins((byte)246,(byte)9,((ToggleButton) findViewById(R.id.toggle_A4_S_AP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_A4_S_AP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
	    
	}
	
	public void ioA5(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/			
			updatePins((byte)244,(byte)9,((ToggleButton) findViewById(R.id.toggle_A5_S_AP)).isChecked(), false);
			updatePins((byte)246,(byte)10,((ToggleButton) findViewById(R.id.toggle_A5_S_AP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_A5_S_AP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
				
	}
	
	public void ioA6(View view){

		updatePins((byte)244,(byte)11,((ToggleButton) findViewById(R.id.toggle_A6_S_AP)).isChecked(), false);
		
	}
	
	public void ioA7(View view){

		updatePins((byte)244,(byte)10,((ToggleButton) findViewById(R.id.toggle_A7_S_AP)).isChecked(), false);
		
	}
	
	public void sA0(View view){
				
		updatePins((byte)144,(byte)1,((ToggleButton) findViewById(R.id.toggle_A0_IO_AP)).isChecked(), false);
	    
	}

	public void sA1(View view){

		updatePins((byte)144,(byte)2,((ToggleButton) findViewById(R.id.toggle_A1_IO_AP)).isChecked(), false);

	}
	
	public void sA2(View view){

		updatePins((byte)144,(byte)3,((ToggleButton) findViewById(R.id.toggle_A2_IO_AP)).isChecked(), false);
		
	}
	
	public void sA3(View view){

		updatePins((byte)144,(byte)4,((ToggleButton) findViewById(R.id.toggle_A3_IO_AP)).isChecked(), false);
		
	}
	
	public void sA4(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)144,(byte)8,((ToggleButton) findViewById(R.id.toggle_A4_IO_AP)).isChecked(), false);
			updatePins((byte)146,(byte)9,((ToggleButton) findViewById(R.id.toggle_A4_IO_AP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_A4_IO_AP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
			    
	}

	public void sA5(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)144,(byte)9,((ToggleButton) findViewById(R.id.toggle_A5_IO_AP)).isChecked(), false);
			updatePins((byte)146,(byte)10,((ToggleButton) findViewById(R.id.toggle_A5_IO_AP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_A5_IO_AP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
				
	}
	
	public void sA6(View view){

		updatePins((byte)144,(byte)11,((ToggleButton) findViewById(R.id.toggle_A6_IO_AP)).isChecked(), false);
						
	}
	
	public void sA7(View view){

		updatePins((byte)144,(byte)10,((ToggleButton) findViewById(R.id.toggle_A7_IO_AP)).isChecked(), false);
				
	}
	
	public void aA0(View view){

		updatePins((byte)244,(byte)1,((CheckBox) findViewById(R.id.checkBox_A0)).isChecked(), true);
		
			    
	}

	public void aA1(View view){

		updatePins((byte)244,(byte)2,((CheckBox) findViewById(R.id.checkBox_A1)).isChecked(), true);
	
	}
	
	public void aA2(View view){

		updatePins((byte)244,(byte)3,((CheckBox) findViewById(R.id.checkBox_A2)).isChecked(), true);

	}
	
	public void aA3(View view){

		updatePins((byte)244,(byte)4,((CheckBox) findViewById(R.id.checkBox_A3)).isChecked(), true);
		
	}
	
	public void aA4(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)244,(byte)8,((CheckBox) findViewById(R.id.checkBox_A4)).isChecked(), true);
			updatePins((byte)246,(byte)9,((CheckBox) findViewById(R.id.checkBox_A4)).isChecked(), true);
		}else{
			CheckBox box = (CheckBox) findViewById(R.id.checkBox_A4);
			box.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
		
	}

	public void aA5(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)244,(byte)9,((CheckBox) findViewById(R.id.checkBox_A5)).isChecked(), true);
			updatePins((byte)246,(byte)10,((CheckBox) findViewById(R.id.checkBox_A5)).isChecked(), true);
		}else{
			CheckBox box = (CheckBox) findViewById(R.id.checkBox_A5);
			box.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
			
	}
	
	public void aA6(View view){

		updatePins((byte)244,(byte)11,((CheckBox) findViewById(R.id.checkBox_A6)).isChecked(), true);
	
	}
		
	public void aA7(View view){

		updatePins((byte)244,(byte)10,((CheckBox) findViewById(R.id.checkBox_A7)).isChecked(), true);
		
	}
	
	public void ioD0(View view){
		
		updatePins((byte)246,(byte)2,((ToggleButton) findViewById(R.id.toggle_D0_IO_DP)).isChecked(), false);
		
	}

	public void ioD1(View view){

		updatePins((byte)246,(byte)3,((ToggleButton) findViewById(R.id.toggle_D1_IO_DP)).isChecked(), false);
		
	}
	
	public void ioD2(View view){

		updatePins((byte)246,(byte)4,((ToggleButton) findViewById(R.id.toggle_D2_IO_DP)).isChecked(), false);
		
	}
	
	public void ioD3(View view){

		updatePins((byte)246,(byte)5,((ToggleButton) findViewById(R.id.toggle_D3_IO_DP)).isChecked(), false);
		
	}
	
	public void ioD4(View view){

		updatePins((byte)246,(byte)6,((ToggleButton) findViewById(R.id.toggle_D4_IO_DP)).isChecked(), false);
		
	}

	public void ioD5(View view){

		updatePins((byte)246,(byte)7,((ToggleButton) findViewById(R.id.toggle_D5_IO_DP)).isChecked(), false);
		
	}
	
	public void ioD6(View view){

		updatePins((byte)246,(byte)8,((ToggleButton) findViewById(R.id.toggle_D6_IO_DP)).isChecked(), false);
		
	}
	
	public void ioD7(View view){

		updatePins((byte)246,(byte)11,((ToggleButton) findViewById(R.id.toggle_D7_IO_DP)).isChecked(), false);
		
	}

	public void ioD8(View view){

		updatePins((byte)244,(byte)13,((ToggleButton) findViewById(R.id.toggle_D8_IO_DP)).isChecked(), false);
		
	}

	public void ioD9(View view){

		updatePins((byte)244,(byte)14,((ToggleButton) findViewById(R.id.toggle_D9_IO_DP)).isChecked(), false);
		
	}
	
	public void ioD10(View view){
		
		updatePins((byte)249,(byte)9,((ToggleButton) findViewById(R.id.toggle_D10_IO_DP)).isChecked(), false);
		
	}
	
	public void ioD11(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)249,(byte)8,((ToggleButton) findViewById(R.id.toggle_D11_IO_DP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_D11_IO_DP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
		
	}
	
	public void ioD12(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)249,(byte)7,((ToggleButton) findViewById(R.id.toggle_D12_IO_DP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_D12_IO_DP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
		
	}

	public void ioD13(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)249,(byte)6,((ToggleButton) findViewById(R.id.toggle_D13_IO_DP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_D13_IO_DP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
		
	}
	
	public void ioDled(View view){

		updatePins((byte)246,(byte)1,((ToggleButton) findViewById(R.id.toggle_DLED_IO_DP)).isChecked(), false);
		
	}
	
	public void sD0(View view){

		updatePins((byte)146,(byte)2,((ToggleButton) findViewById(R.id.toggle_D0_S_DP)).isChecked(), false);
		
	}

	public void sD1(View view){

		updatePins((byte)146,(byte)3,((ToggleButton) findViewById(R.id.toggle_D1_S_DP)).isChecked(), false);
		
	}
	
	public void sD2(View view){

		updatePins((byte)146,(byte)4,((ToggleButton) findViewById(R.id.toggle_D2_S_DP)).isChecked(), false);
	}
	
	public void sD3(View view){

		updatePins((byte)146,(byte)5,((ToggleButton) findViewById(R.id.toggle_D3_S_DP)).isChecked(), false);
		
	}
	
	public void sD4(View view){
		
		updatePins((byte)146,(byte)6,((ToggleButton) findViewById(R.id.toggle_D4_S_DP)).isChecked(), false);
	    
	}

	public void sD5(View view){
		
		updatePins((byte)146,(byte)7,((ToggleButton) findViewById(R.id.toggle_D5_S_DP)).isChecked(), false);

	}
	
	public void sD6(View view){
		
		updatePins((byte)146,(byte)8,((ToggleButton) findViewById(R.id.toggle_D6_S_DP)).isChecked(), false);
		
	}
	
	public void sD7(View view){

		updatePins((byte)146,(byte)11,((ToggleButton) findViewById(R.id.toggle_D7_S_DP)).isChecked(), false);
		
	}

	public void sD8(View view){

		updatePins((byte)144,(byte)13,((ToggleButton) findViewById(R.id.toggle_D8_S_DP)).isChecked(), false);
		
	}

	public void sD9(View view){

		updatePins((byte)144,(byte)14,((ToggleButton) findViewById(R.id.toggle_D9_S_DP)).isChecked(), false);
		
	}
	
	public void sD10(View view){

		updatePins((byte)149,(byte)9,((ToggleButton) findViewById(R.id.toggle_D10_S_DP)).isChecked(), false);
		
	}
	
	public void sD11(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)149,(byte)8,((ToggleButton) findViewById(R.id.toggle_D11_S_DP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_D11_S_DP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
		
	}
	
	public void sD12(View view){

		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)149,(byte)7,((ToggleButton) findViewById(R.id.toggle_D12_S_DP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_D12_S_DP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
		
	}

	public void sD13(View view){
		
		if(!isWiFi){ /* If Communication is done over WiFi, these pins are used to control the WiFi module*/
			updatePins((byte)149,(byte)6,((ToggleButton) findViewById(R.id.toggle_D13_S_DP)).isChecked(), false);
		}else{
			ToggleButton Button = (ToggleButton) findViewById(R.id.toggle_D13_S_DP);
			Button.setChecked(false);
			
			Toast errorToast = Toast.makeText(getApplicationContext(), "This Pin is used for Wifi Communication, can only use when in USB mode", Toast.LENGTH_SHORT);
			errorToast.setGravity(Gravity.CENTER,0,0);
			errorToast.show();
			
		}
		
	}	
	
	public void sDled(View view){

		updatePins((byte)146,(byte)1,((ToggleButton) findViewById(R.id.toggle_DLED_S_DP)).isChecked(), false);
		
	}	
	
	
	
	
	/* This is the server Thread which receives the stream of data coming from somewhere, using UDP packets,
	 * we receive them in the following format:
	 * [100][analogValue0][analogValue12]...[analogValue7][200][digitalState0][digitalState]...[digitalState21]
	 * More information about this format in http://iobridger.wordpress.com 
	 * Java deals only with signed integers, so 100 is actually seen as -56 */	
	
	public class Tserver  extends Thread implements Runnable {
	        
	
	        private  Bundle bundleTserver;
	        private  DatagramSocket socketC;
	        private  DatagramPacket packet;
	        
	        public int portclient = 0;
	        public int firstByte;
	        public String ipString;
	        
	        Handler handler;
	        Message msg ;
	        
	        int position 	= -1;
	        int flag 		= 0;
	        int value 		= 0;
	        int counter 	= 0;
	        int counterboolean = 0;
	        
	        @Override
	        public void run() {
	        	super.run();
	        	
	        	/* Creating and isntance of this thread to later use it to cycle the activity */
		    	Thread thisThread = Thread.currentThread();
		    	
		    	/* Obtain messages from handler in the main thread */
		    	Message msg = handler.obtainMessage();
		    	
		    	/* Create a bundle to send to the main thread through the handler */
				bundleTserver = new Bundle();
	        	        	    	
				try {              
	                        
		            /* Create new UDP-Socket */                       
		            socketC = new DatagramSocket(portclient);
		               	
		             /* By magic we know, how much data will be waiting for us */
		            byte	[] dataInput 	 = new byte		[1];
		            int 	[] databuffer 	 = new int		[8];
		            boolean	[] booleanBuffer = new boolean	[22];
		             
		            
		             /* Prepare a UDP-Packet that can
		              * contain the data we want to receive 	             
		              * Creating packet in which to receive data*/
		             packet = new DatagramPacket(dataInput, dataInput.length);	
		             
		             while(serverThread1 == thisThread) {  	
		          	   
		            	 /* Receive the UDP-Packet */
		          		 socketC.receive(packet);	          		 
		          		 
		          		/* This logic distributes the data input depending on its format, a -56 (200) is received before
		          		 * the analogue values, and a 100 is received before the digital values, so this numbers are used
		          		 * to demultiplex the information.  */
		          		 if(flag == 1 || dataInput[0] == -56){
		          			 
		          			 if(counter == 5){
		          			 
		              		 switch(position){
		              		 case 0:
		              			value = 32 * dataInput[0];
		              			position++;
		              			 break;
		              		case 1:	                      			
		              			databuffer[0] = dataInput[0] + value;
		              			position++;
		              			 break;
		              		case 2:
		              			value = 32 * dataInput[0];
		              			position++;
		              			 break;
		              		case 3:
		              			databuffer[1] = dataInput[0] + value;
		              			position++;
		              			 break;
		              		case 4:
		              			value = 32 * dataInput[0];
		              			position++;
		              			 break;
		              		case 5:
		              			databuffer[2] = dataInput[0] + value;
		              			position++;
		              			 break;
		              		case 6:
		              			value = 32 * dataInput[0];
		              			position++;
		              			 break;
		              		case 7:
		              			databuffer[3] = dataInput[0] + value;
		              			position++;
		              			 break;
		              		case 8:
		              			value = 32 * dataInput[0];
		              			position++;
		              			 break;
		              		case 9:
		              			databuffer[4] = dataInput[0] + value;
		              			position++;
		              			 break;
		              		case 10:
		              			value = 32 * dataInput[0];
		              			position++;
		              			 break;
		              		case 11:
		              			databuffer[5] = dataInput[0] + value;
		              			position++;
		              			 break;
		              		case 12:
		              			value = 32 * dataInput[0];
		              			position++;
		              			 break;
		              		case 13:
		              			databuffer[6] = dataInput[0] + value;
		              			position++;
		              			 break;
		              		case 14:
		              			value = 32 * dataInput[0];
		              			position++;
		              			 break;
		              		case 15:
		              			databuffer[7] = dataInput[0] + value;
		              			/* Put all databuffer array into the bundle to send over the handler */
		              			bundleTserver.putIntArray("buffer", databuffer);
		              			
		              			/*put the bundle into the message that is passed to the 
		              			 * main activity over the handler */ 
		              			msg = Message.obtain();
		                 		msg.setData(bundleTserver);
		                 		handler.sendMessage(msg);
		                 		
		                 		/* Reset flags of this logic */
		                 		counter 	= 0;
		                 		flag 		= 0;
		                 		position 	= -1;
		              			break;
		              		case -1:
		              			flag 		= 1;
		              			position 	= 0;
		              			break;
		              		 
		              		 } 
		              		 
		          		 }
		          			 else if (counter!=5)	{
		          				 counter++;
		          			 }
		          		 
		          		 } 
		          		 else if(flag == 2 || dataInput[0] == 100){
		          			
		          			 if(counterboolean == 5){
		          			 
		          			 switch(position){
		          			case 0:
		         				 if (dataInput[0] == 1){
		         					 booleanBuffer[position] = true;
		         				 }else{
		         					 booleanBuffer[position] = false;
		         				 }
		                  			position++;
		         				 break; 
		         			case 1:
		          				 if (dataInput[0] == 1){
		          					 booleanBuffer[position] = true;
		          				 }else{
		          					 booleanBuffer[position] = false;
		          				 }
		          				position++;
		     				 break;
		       				case 2:
		          				 if (dataInput[0] == 1){
		          					 booleanBuffer[position] = true;
		          				 }else{
		          					 booleanBuffer[position] = false;
		          				 }
		                  			position++;
		     				 break; 
		       			 	case 3:
		           				 if (dataInput[0] == 1){
		           					 booleanBuffer[position] = true;
		           				 }else{
		           					 booleanBuffer[position] = false;
		           				 }
		                  			position++;
		      				 break;
		        			 case 4:
		          				 if (dataInput[0] == 1){
		          					 booleanBuffer[position] = true;
		          				 }else{
		          					 booleanBuffer[position] = false;
		          				 }
		                  			position++;
		     				 break; 
		          			 case 5:
		         				 if (dataInput[0] == 1){
		         					 booleanBuffer[position] = true;
		         				 }else{
		         					 booleanBuffer[position] = false;
		         				 }
		                  			position++;
		         				 break; 
		          			case 6:
		          				 if (dataInput[0] == 1){
		          					 booleanBuffer[position] = true;
		          				 }else{
		          					 booleanBuffer[position] = false;
		          				 }
		                  			position++;
		      				 break;
		        			 case 7:
		          				 if (dataInput[0] == 1){
		          					 booleanBuffer[position] = true;
		          				 }else{
		          					 booleanBuffer[position] = false;
		          				 }
		                  			position++;
		      				 break; 
		        			case 8:
		           				 if (dataInput[0] == 1){
		           					 booleanBuffer[position] = true;
		           				 }else{
		           					 booleanBuffer[position] = false;
		           				 }
		                  			position++;
		       				 break;
		         			 case 9:
		          				 if (dataInput[0] == 1){
		          					 booleanBuffer[position] = true;
		          				 }else{
		          					 booleanBuffer[position] = false;
		          				 }
		                  			position++;
		      				 break; 
		        			case 10:
		           				 if (dataInput[0] == 1){
		           					 booleanBuffer[position] = true;
		           				 }else{
		           					 booleanBuffer[position] = false;
		           				 }
		                  			position++;
		       				 break;
		         			 case 11:
		           				 if (dataInput[0] == 1){
		           					 booleanBuffer[position] = true;
		           				 }else{
		           					 booleanBuffer[position] = false;
		           				 }
		                  			position++;
		           				 break; 
		         			case 12:
		        				 if (dataInput[0] == 1){
		        					 booleanBuffer[position] = true;
		        				 }else{
		        					 booleanBuffer[position] = false;
		        				 }
		              			position++;
		        				 break;
		          			 case 13:
		          				 if (dataInput[0] == 1){
		          					 booleanBuffer[position] = true;
		          				 }else{
		          					 booleanBuffer[position] = false;
		          				 }
		                  			position++;
		      				 break; 
		        			case 14:
		           				 if (dataInput[0] == 1){
		           					 booleanBuffer[position] = true;
		           				 }else{
		           					 booleanBuffer[position] = false;
		           				 }
		                  			position++;
		       				 break;
		         			 case 15:
		         				 if (dataInput[0] == 1){
		         					 booleanBuffer[position] = true;
		         				 }else{
		         					 booleanBuffer[position] = false;
		         				 }
		              			position++;
		       				 break; 
		         			case 16:
		        				 if (dataInput[0] == 1){
		        					 booleanBuffer[position] = true;
		        				 }else{
		        					 booleanBuffer[position] = false;
		        				 }
		              			position++;
		        				 break;
		          			 case 17:
		           				 if (dataInput[0] == 1){
		           					 booleanBuffer[position] = true;
		           				 }else{
		           					 booleanBuffer[position] = false;
		           				 }
		                  			position++;
		                  		break; 
		         			case 18:
		        				 if (dataInput[0] == 1){
		        					 booleanBuffer[position] = true;
		        				 }else{
		        					 booleanBuffer[position] = false;
		        				 }
		              			position++;
		        				 break;
		          			 case 19:
		        				 if (dataInput[0] == 1){
		        					 booleanBuffer[position] = true;
		        				 }else{
		        					 booleanBuffer[position] = false;
		        				 }
		              			position++;
		        				 break; 
		          			case 20:
		         				 if (dataInput[0] == 1){
		         					 booleanBuffer[position] = true;
		         				 }else{
		         					 booleanBuffer[position] = false;
		         				 }
		              			position++;
		         				 break;
		          			 case 21:
		          				 if (dataInput[0] == 1){
		          					 booleanBuffer[position] = true;
		          				 }else{
		          					 booleanBuffer[position] = false;
		          				 }
		          				 
		          				 /* put the bollean array into the bundle to send over the handler */
		          				 bundleTserver.putBooleanArray("booleanBuffer", booleanBuffer);
		          				 
		          				 /* Put the bundle in a message and send over the handler */
		          				 Message msg1 = Message.obtain();
		                 		 msg1.setData(bundleTserver);
		                 		 handler.sendMessage(msg1);
		                 		 
		                 		 /* Reset the flags of this logic */
		          				 flag 			= 0;
		          				 position 		= -1;
		          				 counterboolean = 0;
		          				 break;
		          			 case -1:
		          				 flag 		= 2;
		          				 position 	= 0;
		          				 break;
		          			 
		          			 }
		          			 
		          			 
		          			 
		          		 }else if (counterboolean != 5){
		           			counterboolean++; 
		          			 
		          		 }
		             }
		             
		         }
		         socketC.close();
	                }
	                catch (Exception e) {
	                        Log.e("UDP", "server: Error ", e);
	                                             	
	        	  }       
	                
	        }
	        
	        /* Public constructor of the thread class for the Server  */
	        public Tserver(int port, String ip, Handler handler){
	        	
	        	this.portclient = port;
	        	this.ipString = ip;
	        	this.handler = handler;
	        }
	     
	        /* This functions closes the thread by making serverThread1 null */
	        public  void closeThread(){
	        	
	        	serverThread1 = null;
	        	socketC.close();
	        	
	        }
	        
        
	}
	
	
	/* The interface code follows below, it includes clases to use the PagerAdapter and fragments within it. */


	/* ViewPager and related classes start */

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

			
		@Override  
		public Fragment getItem(int position) {  
		    Fragment fragment = new Fragment();  
		    switch (position) {  
		    case 0:  
		    	return fragment = new Fragment1();		        
		    case 1:  
		        return fragment = new Fragment2();  
		    case 2:  
		        return fragment = new Fragment3();  
		    case 3:  
		        return fragment = new Fragment4(); 
		    case 4:  
		        return fragment = new Fragment5(); 
		    default:  
		        break;  
		    }  
		    return fragment;  
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}	
		
	}
		

	public static class Fragment1 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public Fragment1() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.analogue_inputs, container, false);
			
			return rootView;
		}
	}

	public static class Fragment2 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public Fragment2() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.analogue_port, container, false);
			return rootView;
		}
	}

	public static class Fragment3 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public Fragment3() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.digital_port_0, container, false);
			return rootView;
		}
	}

	public  static class Fragment4 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public Fragment4() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.digital_port_1, container, false);			
			return rootView;
		}
	}

	public  static class Fragment5 extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public Fragment5() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.digital_outputs, container, false);			
			return rootView;
		}
	}
	
	
}


