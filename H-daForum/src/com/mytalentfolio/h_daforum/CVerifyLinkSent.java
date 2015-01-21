
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
 * <p><strong> The Class CVerifyLinkSent.</strong></br>

 * this activity is launched after successful registration
 * of the user. Now user has to verify himself by going to 
 * link sent on his email ID.
 * FUTURE EXTENSION NOTE: the button resend code has to implemented
 *  </p>
 * 
 *
 * @author Ankit Goyal
 * @version 1.1
 *
 */
public class CVerifyLinkSent extends Activity {

	/** The phone nr. */
	String phoneNr="";
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cverify_link_sent);
		
		Intent i = getIntent();
		getPhnNrFromIntent(i);
		
		makeLoginButtonClickable();
		makeResendCodeButtonClickable();
	}
	
	/**
	 * Gets the phn nr from intent.
	 *
	 * @param i the intent
	 * @return the phn nr from intent
	 */
	private void getPhnNrFromIntent(Intent i){
		phoneNr = i.getStringExtra("phoneNr");
	}
	
	/**
	 * Make login button clickable.
	 */
	private void makeLoginButtonClickable(){
		Button login = (Button) findViewById(R.id.LoginFromVerifyLink);
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchLoginActivity();
			}
		});
	}
	
	/**
	 * Launch login activity.
	 */
	private void launchLoginActivity(){
		Intent launchActivityLogin = new Intent(CVerifyLinkSent.this, LoginActivity.class);
		startActivity(launchActivityLogin);
	}
	
	/**
	 * Make resend code button clickable.
	 */
	private void makeResendCodeButtonClickable(){
		Button login = (Button) findViewById(R.id.resendCode);
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resendCode();
			}
		});
	}
	
	/**
	 * Resend code.
	 * Left for future extension. 
	 */
	private void resendCode(){
	//writ code for resending the code to mobile phone	
	}
}
