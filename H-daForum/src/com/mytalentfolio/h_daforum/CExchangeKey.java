
package com.mytalentfolio.h_daforum;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * <p><strong>The Class CExchangeKey.</strong></br>
 * Here a user can see his exchange key
 * and can verify his friend by entering 
 * his friend's email ID and exchange key<p>
 * 
 * @author Ankit Goyal
 * @version 1.1
 */
public class CExchangeKey extends Activity {

	/** The user id and course id. */
	String courseID, userID;
	
	/** The context of the activity. */
	Context contextActivity;
	
	/** The user's frnd email verify layout and progress layout. */
	LinearLayout progressLayout, userFrndEmailVerfyLayout;
	
	/** The my library functions class object. */
	CMyLibFunctions myLibFunctions;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cexchange_key);

		contextActivity = this;
		myLibFunctions = new CMyLibFunctions();
		
		getLayouts();
		
		getIntentData();
		
		//starting ascyn task to fetch the user key 
		FetchMyKey myKey = new FetchMyKey();
		myKey.execute();
		
		//make the verification link clickable
		makeVerifyButtonClickable();
	}

	/**
	 * get the layouts and save them in variables
	 */
	private void getLayouts(){
		progressLayout = (LinearLayout) findViewById(R.id.progressOfVerifyUrFrnd);
		userFrndEmailVerfyLayout = (LinearLayout) findViewById(R.id.verifyFrndContainer);
	}
	
	/**
	 * Gets the intent data.
	 */
	private void getIntentData(){
		Intent i = getIntent();
		courseID = i.getStringExtra("courseID");
		userID = i.getStringExtra("userID");
	}

	
	/**
	 * The Class FetchMyKey.
	 * async task for fetching the exchange key from server
	 */
	public class FetchMyKey extends AsyncTask<Void, Void, String> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String doInBackground(Void... params) {
			return sendDataToServer();
		}
		
		/**
		 * Send data to server.
		 *
		 * @return the string of data rcvd from server
		 */
		private String sendDataToServer(){	
			CconnectToServer sendUserIDAndGetkey = new CconnectToServer();
			String exchangeKeyResult = sendUserIDAndGetkey.connect("exchangeKey.php", "1", userID);
			Vector<String> exchangeKeyVector = new Vector<String>();
			exchangeKeyVector = sendUserIDAndGetkey.extractDataFromJson(exchangeKeyResult, "exchangeKey");
			
			exchangeKeyResult = exchangeKeyVector.get(0);
			return exchangeKeyResult;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			if(CExceptionHandling.ExceptionState!=ExceptionSet.NO_EXCEPTION){
				CExceptionHandling.showExceptionToast(getApplicationContext());
			}else{
			TextView exchangeKeyView = (TextView) findViewById(R.id.exchangeKeyShow);
			String textTobeShown = "";
			textTobeShown = textTobeShown.concat(result);
			exchangeKeyView.setText(textTobeShown);
		}
	}
	}
	
	/**
	 * Make verify button clickable.
	 */
	private void makeVerifyButtonClickable(){
		Button verifyUserButton = (Button) findViewById(R.id.verifyUrFrnd);
		verifyUserButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// start the progress
				myLibFunctions.showProgress(true, progressLayout, userFrndEmailVerfyLayout);
				
				//start async task to verify the user
				VerifyUrFrnd verify = new VerifyUrFrnd();
				verify.execute();
			}
		});
	}
	
	/**
	 * The Class VerifyUrFrnd.
	 * async task for verifying the user's frnds
	 */
	public class VerifyUrFrnd extends AsyncTask<String, Void, String> {

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected String doInBackground(String... params) {
				String verificationResult = getFrndsEmailAndVerfctnCode();
				return verificationResult;
			}
			
			/**
			 * Gets the frnds email and verification code.
			 *
			 * @return the verification result whether the user's frnd is 
			 * verified or not 
			 */
			private String getFrndsEmailAndVerfctnCode(){
				EditText frndEmailEditText = (EditText) findViewById(R.id.frndsEmail);
				EditText frndKeyEditText = (EditText) findViewById(R.id.exchangeKey);
				
				String frndEmail = frndEmailEditText.getText().toString();
				String frndExchangeKey = frndKeyEditText.getText().toString();
				
				return sendDataToServer(frndEmail, frndExchangeKey);
			}
			
			/**
			 * Send data to server.
			 *
			 * @param frndEmail the friend email
			 * @param frndExchangeKey the friend exchange key
			 * @return the verification result whether the user's frnd is 
			 * verified or not 
			 */
			private String sendDataToServer(String frndEmail, String frndExchangeKey){	
				CconnectToServer sendFrndEmailAndKey = new CconnectToServer();
				String verificationResult = sendFrndEmailAndKey.connect("exchangeKey.php", "2", userID,frndEmail, frndExchangeKey);
				return verificationResult;
			}
			
			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(String result) {
				String textToShow;
				LinearLayout verifyFrndLayout = (LinearLayout) findViewById(R.id.resultContainer);
				if(CExceptionHandling.ExceptionState!=ExceptionSet.NO_EXCEPTION){
					CExceptionHandling.showExceptionToast(getApplicationContext());
				}else
				//if(result.equals("\n[\"1\"]\n")){
					if(result.equals("[\"1\"]")){
							textToShow = "Congrats!! Your Freind is now verified. \n Please note that to get highest authentication level, \n you need to get verified by atleast 4 of your registered freinds.";	
					myLibFunctions.displayResult(textToShow, verifyFrndLayout,contextActivity);
					myLibFunctions.showProgress(false, progressLayout, userFrndEmailVerfyLayout);
				}
				//else if(result.equals("\n[\"userVerifyHimself\"]\n")){
				else if(result.equals("[\"userVerifyHimself\"]")){
							textToShow = "Opps!! You cannot verify yourself. Give this exchange key to your friend along with your email ID to verify you.";	
					myLibFunctions.displayResult(textToShow, verifyFrndLayout,contextActivity);
					myLibFunctions.showProgress(false, progressLayout, userFrndEmailVerfyLayout);
				}
				//else if(result.equals("\n[\"userAlreadyVerifiedFrnd\"]\n")){
				else if(result.equals("[\"userAlreadyVerifiedFrnd\"]")){
					textToShow = "Opps!! You have already verified this user";	
					myLibFunctions.displayResult(textToShow, verifyFrndLayout,contextActivity);
					myLibFunctions.showProgress(false, progressLayout, userFrndEmailVerfyLayout);
				}
				else{
					textToShow = "Invalid email id or verification code. Please try again.";
					myLibFunctions.displayResult(textToShow, verifyFrndLayout, contextActivity);
					myLibFunctions.showProgress(false, progressLayout, userFrndEmailVerfyLayout);	
				}
			}
		}
}
