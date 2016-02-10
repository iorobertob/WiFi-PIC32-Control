package io.pinguinocontrol;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ConnectionSelect extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Activity Layout to display */
		setContentView(R.layout.activity_connection_select);
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
	
	/* This method makes the application branch into the control using USB communication */
	public void usb(View view){
		/* Launches next class and layout */
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		
	}
	
	/* This method makes the application branch into the control using WiFi communication */
	public void wifi(View view){
		/* Launches next class and layout */
		Intent intent = new Intent(this, AvailablePinguinos.class);
		startActivity(intent);
	}
	
}
