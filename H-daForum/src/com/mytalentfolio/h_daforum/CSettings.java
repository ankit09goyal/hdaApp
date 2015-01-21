
package com.mytalentfolio.h_daforum;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
 * <p><strong> The Class CSettings.</strong></br>
 *this class shows the profile of the user
 *  </p>
 * 
 *
 * @author Zeeshan Haider
 * @version 1.1
 */
public class CSettings extends Activity {
	
	/** The user id, course id. */
	String courseID="", userID="";
   
   /** The mylist. */
   LinearLayout mylist;
   
   /** The progress bar. */
   LinearLayout progressBar;
	
	/** The display. */
	displayContent display;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		progressBar = (LinearLayout) findViewById(R.id.BprogressOfForumList);
		mylist = (LinearLayout) findViewById(R.id.TableLayout1);
		showProgress(true);
        getIntentData();
		extraxtcUserInfo();
	}

	/**
	 * Gets the intent data.
	 *
	 * @return the intent data
	 */
	private void getIntentData() {
		Intent i = getIntent();
		courseID = i.getStringExtra("courseID");
		userID = i.getStringExtra("userID");
		
	}

	/**
	 * Extract user info.
	 */
	private void extraxtcUserInfo() {
		Intent i = getIntent();
		display = new displayContent();
		display.execute(i);
		
	}
	
	/**
	 * The Class displayContent is used to get data from server and display.
	 */
	public class displayContent extends AsyncTask<Intent, Void, String[]> {

		/** The result. */
		String[] result = new String[5];
		
		/** The parameters for data extraction. */
		String userName="", phoneNum="", courseName="", Batchname="", UniversityName="";

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String[] doInBackground(Intent... params) {
			getIntentAndDisplayData(params[0]);
			result[0]= userName;
			result[1]= phoneNum;
			result[2]= courseName;
			result[3]= Batchname;
			result[4]= UniversityName;
			
			return result;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String[] result) {
			
			showProgress(false);
			TextView username = (TextView) findViewById(R.id.nameofuser);
			username.setText(result[0]);
			TextView phonenm = (TextView) findViewById(R.id.phonenumber);
			phonenm.setText(result[1]);
			TextView uni = (TextView) findViewById(R.id.universityname);
			uni.setText(result[4]);
			TextView course = (TextView) findViewById(R.id.coursename);
			course.setText(result[2]);
			TextView batchnm = (TextView) findViewById(R.id.batchname);
			batchnm.setText(result[3]);
		
		}

		/**
		 * Gets the intent and display data.
		 *
		 * @param i the i
		 * @return the intent and display data
		 */
		private void getIntentAndDisplayData(Intent i) {
			if (!isCancelled()) {
				courseID = i.getStringExtra("courseID");
				userID = i.getStringExtra("userID");
				getDataFromServer(courseID, userID);
			}
		}

		// get all data in jason format from server
		/**
		 * Gets the data from server.
		 *
		 * @param cID the c id
		 * @param uID the u id
		 * @return the data from server
		 */
		private void getDataFromServer(String cID,String uID) {
			if (!isCancelled()) {
				
				String data_email_phone, data_course_batch, data_university;
				
				CconnectToServer getData = new CconnectToServer();
				data_email_phone = getData.connect("settings.php", "0", uID);
				extractDataFromJsonEmailPhone(data_email_phone);
				
				CconnectToServer getData2 = new CconnectToServer();
				data_course_batch = getData2.connect("settings.php", "1", cID);
				extractDataFromJsonCourseBatch(data_course_batch);
				
				CconnectToServer getData3 = new CconnectToServer();
				data_university = getData3.connect("settings.php", "2", cID);
				extractDataFromJsonUniversity(data_university);
				
			}
		}

		/**
		 * Extract data from json .
		 *
		 * @param data_university
		 */
		private void extractDataFromJsonUniversity(String data_university) {
			if (!isCancelled()) {

				// extracting email
				Vector<String> uniValue = new Vector<String>();
				CconnectToServer extractUni = new CconnectToServer();

				uniValue = extractUni.extractDataFromJson(
						data_university, "UniversityName");
				UniversityName = uniValue.get(0);
			
				

			}
			
		}

		/**
		 * Extract data from json course batch.
		 * extracting email and phone number here
		 * @param data_course_batch the data_course_batch
		 */
		private void extractDataFromJsonCourseBatch(String data_course_batch) {
			if (!isCancelled()) {

				// extracting email
				Vector<String> courseValue = new Vector<String>();
				CconnectToServer extractCourse = new CconnectToServer();

				courseValue = extractCourse.extractDataFromJson(
						data_course_batch, "CourseName");
				courseName = courseValue.get(0);
				
				//phone number
				Vector<String> batch_value = new Vector<String>();
				CconnectToServer extractPhone = new CconnectToServer();

				batch_value = extractPhone.extractDataFromJson(
						data_course_batch, "Batch");
				Batchname = batch_value.get(0);
				

			}
			
		}

		
		
		/**
		 * Extract data from json email phone.
		 * extract data rcvd from server in JSON format
		   return the data extracted
		   email and phone extraction
		 * @param courseIData the course i data
		 */
		private void extractDataFromJsonEmailPhone(String courseIData) {
			if (!isCancelled()) {

				// extracting email
				Vector<String> storeExtractedValues = new Vector<String>();
				CconnectToServer extractData = new CconnectToServer();

				storeExtractedValues = extractData.extractDataFromJson(
						courseIData, "username");
				userName = storeExtractedValues.get(0);
				TextView username = (TextView) findViewById(R.id.nameofuser);
				username.setText(userName);
				
				//phone number
				Vector<String> phone_value = new Vector<String>();
				CconnectToServer extractPhone = new CconnectToServer();

				phone_value = extractPhone.extractDataFromJson(
						courseIData, "phoneNr");
				phoneNum = phone_value.get(0);
				

				
				

			}
		}

	}
	
	/**
	 * Show progress.
	 *
	 * @param show the show
	 */
	public void showProgress(boolean show) {
		if (show) {
			mylist.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			mylist.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}

}
