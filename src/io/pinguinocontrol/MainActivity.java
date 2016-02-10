package io.pinguinocontrol;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public final static String EXTRA_MESSAGE 	= "io.PinguinoControl.MESSAGE";
	public final static String PORT 			= "io.PinguinoControl.PORT";
	
	public EditText editPort;
	public EditText editIP;
	
	public SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*  Launch layout */
		setContentView(R.layout.activity_main);
		
		
	}
	
	public void onResume(){
		super.onResume();
		
		/* Get typeface from resources to set button's font*/
		Typeface face=Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf"); 
		
		/* Obtain views to insert default or saved values*/
		editIP = (EditText) findViewById(R.id.Edit_IP_1);
		editPort = (EditText) findViewById(R.id.Edit_Port_1);
		
		/* Custom typeface, roboto this time*/
		Button connectButton = (Button) findViewById(R.id.Button_a2);
		connectButton.setTypeface(face);
		
		/* Get values saved in Shared Preferences, and set them to the Views*/
		settings = getApplicationContext().getSharedPreferences("Pref_Data", 0);
		int savedPort = settings.getInt("PortNumber", 10001);
		String savedIP = settings.getString("IPAddress", "Type in IP Address");
		String savedPorto = Integer.toString(savedPort);
		
		if(savedIP != "Type in IP Address"){		
			editIP.setText(savedIP);
		}
		if(savedPort != 0){
    		editPort.setText(savedPorto);
		}
		
	}
	
	/* When click on the button, trying and connecting to the IP and Port specified in the
	 * TextEdit Views, this launches an intent and uses a bundle to send this data that
	 * will be used to create network DatagramPackets and Sockets. This method also saves
	 * the values entered on the EditText Views in SharedPreferences.*/
	public void connect(View view){
		 
		
		 try{
	    	Intent intent = new Intent(this, Connect.class);
	    	
	    	/* Variables extracted from Text Fields */
	    	String ipAddress 	= editIP.getText().toString();
	    	String porto 		= editPort.getText().toString();
	    	int intPort 		= Integer.parseInt(porto);
	    	
	    	/*  Putting variables into the intent to pass on to the next activity */
	    	intent.putExtra("PORT", intPort);
	    	intent.putExtra("IP", ipAddress);
	    	intent.putExtra("ISWIFI",false);
	    	
	    	/* Save data into Preferences*/
	    	SharedPreferences.Editor editor = settings.edit();
	    	editor.putString("IPAddress", ipAddress);
	    	editor.putInt("PortNumber", intPort);

	    	
	    	/* Commit the edits*/
	    	editor.commit();
	    	
	    	startActivity(intent);
	    		    	
		 }catch (NumberFormatException e){
			 
			 Toast errorToast = Toast.makeText(getApplicationContext(), "Type in valid Port", Toast.LENGTH_LONG);
			 errorToast.setGravity(Gravity.CENTER,0,0);
			 errorToast.show();
		 }
	    	
	    	
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
	
	public void onDestroy(){
		super.onDestroy();
	}

}
