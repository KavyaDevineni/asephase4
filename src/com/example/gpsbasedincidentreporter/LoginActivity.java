package com.example.gpsbasedincidentreporter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
	public void validate(View view) throws InterruptedException{
		EditText username=(EditText)findViewById(R.id.usernameedit);
		EditText password=(EditText)findViewById(R.id.passwordedit);
		
		String actualUserName = username.getText().toString().trim();
		String actualPassword = password.getText().toString().trim();
		
		if(actualUserName.equalsIgnoreCase(getString(R.string.usernamevalue)) && actualPassword.equalsIgnoreCase(getString(R.string.paswordvalue))){
			Intent intent=new Intent(this,MainActivity.class);
			TextView statusmessage = (TextView)findViewById(R.id.status);
			statusmessage.setTextColor(Color.GREEN);
			statusmessage.setText("Login Succeeded; Navigating to GPSIncidentsReporter..Please Wait......");
			Thread.sleep(2000);
			startActivity(intent);
		}
			
		else
		{
			TextView statusmessage = (TextView)findViewById(R.id.status);
			statusmessage.setText("Login Failed; Invalid UserName/Password. Please wait for the page to refresh and try again!!");
			Intent intent=new Intent(this,LoginActivity.class);
			startActivity(intent);
		}
	}
}
