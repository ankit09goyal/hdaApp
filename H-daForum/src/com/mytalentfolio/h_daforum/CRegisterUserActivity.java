
package com.mytalentfolio.h_daforum;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 * <p><strong> The Class CRegisterUserActivity.</strong></br>
 *
 * here user can register himself by providing the details </p>
 * 
 *
 * @author Ankit Goyal
 * @version 1.1
 */
public class CRegisterUserActivity extends Activity {

	EditText mEmailIDView, mPhoneNrView, mUniversityView, mGradYearView, mCourseNameView;
	String mEmail, mPhone;



	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cregister_user);

		//setting the context, used to read the file server.crt or localhost.crt
		CconnectToServer.mContext=getApplicationContext();
		
		makeRegisterButtonClickable();
	}
	
	/**
	 * Make register button clickable.
	 */
	private void makeRegisterButtonClickable() {
		Button register = (Button) findViewById(R.id.registerButton);
		register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendDataToServer();
			}
		});
	}

	/**
	 * send user info to the server
	 */
	private void sendDataToServer() {
		mEmailIDView = (EditText) findViewById(R.id.emailID);
		mPhoneNrView = (EditText) findViewById(R.id.phone);
		mUniversityView = (EditText) findViewById(R.id.universityName);
		mGradYearView = (EditText) findViewById(R.id.gradYear);
		mCourseNameView = (EditText) findViewById(R.id.courseName);

		mEmail = mEmailIDView.getText().toString();
		mPhone = mPhoneNrView.getText().toString();
		String univ = mUniversityView.getText().toString();
		String grad_year = mGradYearView.getText().toString();
		String courseName = mCourseNameView.getText().toString();

		boolean valid = checkValidityOfEmailAndPhone(mEmail, mPhone);

		if(valid){
			SendUserInfo userInfo = new SendUserInfo();
			userInfo.execute(mEmail, mPhone, univ, grad_year, courseName);
		}
	}

	/**
	 * Check validity of email and phone.
	 *
	 * @param email the email
	 * @param phone the phone
	 * @return true, if successful
	 */
	private boolean checkValidityOfEmailAndPhone(String email, String phone) {
		View focusView = null;
		boolean cancel = false;

		String emailRegex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@stud.h-da.de";
		
		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailIDView.setError(getString(R.string.error_field_required));
			focusView = mEmailIDView;
			cancel = true;
		} else if (!email.contains("@")) {
			mEmailIDView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailIDView;
			cancel = true;
		} else if(!email.matches(emailRegex)){
			mEmailIDView.setError(getString(R.string.error_invalid_officialEmail));
			focusView = mEmailIDView;
			cancel = true;
		}

		// Check for a valid phone nr..
		String regex = "^[+][0-9]{8,13}$";
		if (TextUtils.isEmpty(phone)) {
			mPhoneNrView.setError(getString(R.string.error_field_required));
			focusView = mPhoneNrView;
			cancel = true;
		}else if (!phone.matches(regex)) {
			mPhoneNrView.setError(getString(R.string.error_invalid_phoneNr));
			focusView = mPhoneNrView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt to register and focus the first
			// form field with an error.
			focusView.requestFocus();
			return false;
		} else {	return true;	}
	}

	/**
	 * async task to send the user details to the server
	 * for verification and registration
	 */
	public class SendUserInfo extends AsyncTask<String, Void, String> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String doInBackground(String... arg0) {
			String userEmail = (String) arg0[0];
			String userPhone = (String) arg0[1];
			String userUniv = (String) arg0[2];
			String userGradYear= (String) arg0[3];
			String userCourseName = (String) arg0[4];

			String result = sendDataToServer(userEmail, userPhone, userUniv, userGradYear, userCourseName);
			return result;
		}

		/**
		 * Send data to server.
		 *
		 * @param userEmail the user email
		 * @param userPhone the user phone
		 * @param userUniv the user univ
		 * @param userGradYear the user grad year
		 * @param userCourseName the user course name
		 * @return the result received from the server
		 */
		private String sendDataToServer(String userEmail, String userPhone, String userUniv, String userGradYear, String userCourseName){	
			CconnectToServer sendUserInfoToServer = new CconnectToServer();
			String result = sendUserInfoToServer.connect("registerUser.php",
					userEmail, userPhone, userUniv, userGradYear, userCourseName);
			return result;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			if(CExceptionHandling.ExceptionState!=ExceptionSet.NO_EXCEPTION){
				CExceptionHandling.showExceptionToast(getApplicationContext());
			}else if(result.equals("[\"1\"]")) {
			//if (result.equals("\n[\"1\"]\n")) {
				launchVerfLinkInfoActivity();
			}
			//else if(result.equals("\n[\"DuplicateEmail\"]\n")) {
			 else if(result.equals("[\"DuplicateEmail\"]")) {
					Toast.makeText(getApplicationContext(),
						"Provided email ID already exists", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(getApplicationContext(),
						"Error in registering at the moment, please try again later", Toast.LENGTH_LONG).show();
			}
		}

		/**
		 * Launch verification link info activity.
		 */
		private void launchVerfLinkInfoActivity() {
			Intent launchActivityVerifyLink = new Intent(CRegisterUserActivity.this, CVerifyLinkSent.class);
			//launchActivityVerifyLink.putExtra("emailID", mEmail);
			launchActivityVerifyLink.putExtra("phoneNr", mPhone);
			startActivity(launchActivityVerifyLink);
		}
	}
}