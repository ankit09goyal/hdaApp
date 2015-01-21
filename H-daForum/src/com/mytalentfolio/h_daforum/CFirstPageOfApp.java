
package com.mytalentfolio.h_daforum;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


// TODO: Auto-generated Javadoc
/**
 * <p>Copyright (c) Hochschule Darmstadt. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.</p>
 *
 * <p>You can redistribute and/or modify the code
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.</p> 
 *
 * <p>Please contact Hochschule Darmstadt if you need additional information or have any
 * questions.</p>
 * 
 * <p><strong>The Class CFirstPageOfApp.</strong></br>
 *
 * Displays the option to the user to login or register
 * This is the launch activity of the App or first activity that user 
 * will see after opening the app</p>
 * 
 * @author Ankit Goyal
 * @version 1.1
 */
public class CFirstPageOfApp extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cfirst_page_of_app);

		makeLoginButtonClickable();
		makeRegisterButtonClickable();	
	}

	/**
	 * Make login button clickable.
	 */
	private void makeLoginButtonClickable(){
		Button login = (Button) findViewById(R.id.Login);
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchLoginActivity();
			}
		});
	}
	
	/**
	 * Make register button clickable.
	 */
	private void makeRegisterButtonClickable(){
		Button register = (Button) findViewById(R.id.Register);
		register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchRegisterActivity();
			}
		});
	}
	
	/**
	 * Launch login activity.
	 */
	private void launchLoginActivity(){
		Intent launchActivityLogin = new Intent(CFirstPageOfApp.this, LoginActivity.class);
		startActivity(launchActivityLogin);
	}
	
	/**
	 * Launch register activity.
	 */
	private void launchRegisterActivity(){
		Intent launchActivityRegister = new Intent(CFirstPageOfApp.this, CRegisterUserActivity.class);
		startActivity(launchActivityRegister);
	}
}
