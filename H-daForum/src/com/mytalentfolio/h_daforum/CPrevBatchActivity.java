
package com.mytalentfolio.h_daforum;
import java.util.Vector;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;



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
 * <p><strong> The Class CPrevBatchActivity.</strong></br>
 * user has ability to select old batch year in this activity
 * course id of the year will be sent to the next activity
 *  </p>
 *
 * 
 * @author Zeeshan Haider
 * @version 1.1 
 */
public class CPrevBatchActivity extends Activity{

	/** The spinner for year selection */
	private Spinner spinner1;
	
	/** To display data. */
	GetCourseIdFromserver display;
	
	/** The current course id,old course id and user id. */
	String userID="", courseID="",currentCourseyear="", currentCourseID="";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.old_batches_selection);
		//addItemsOnSpinner2(); //for future
		spinnerValue();
		makeCreateTopicButtonClickable();

	}

	/**
	 * Gets the course id from the selected batch.
	 */
	private void getCourseID() {

		Intent i = getIntent();
		display = new GetCourseIdFromserver();
		display.execute(i);


	}

	/**
	 * The Class gets the course id from the server
	 * by looking at the batch that has been selected.
	 */
	public class GetCourseIdFromserver extends AsyncTask<Intent, Void, String[]> {

		/** The result. */
		String[] result = new String[2];

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String[] doInBackground(Intent... params) {
			getIntentAndDisplayData(params[0]);
			return result;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String[] result) {
			SetExtractedData(result[0]);
		}

		/**
		 * Gets the intent and display data.
		 *
		 *intent i
		 */
		private void getIntentAndDisplayData(Intent i) {
			if (!isCancelled()) {
				courseID = i.getStringExtra("courseID");
				userID = i.getStringExtra("userID");
				getDataFromServer(currentCourseyear, courseID, userID);
			}
		}

		// get all data in jason format from server
		/**
		 * Gets the data from server.
		 *
		 * @param year the year
		 * @param cID the c id
		 * @param uID the u id
		 * @return the data from server
		 */
		private void getDataFromServer(String year, String cID,String uID) {
			if (!isCancelled()) {
				String courseIData;
				CconnectToServer getData = new CconnectToServer();
				courseIData = getData.connect("otherBatch.php", "0",
						year, uID, cID);
				extractDataFromJson(courseIData);
			}
		}

		
		/**
		 * Extract data from json.
		 * extract data rcvd from server in JSON format
		 * return the data extracted
		 * extracting forum details
		 * @param courseIData the course i data
		 */
		private void extractDataFromJson(String courseIData) {
			if (!isCancelled()) {
				String currentCourseIDd="";

				Vector<String> storeExtractedValues = new Vector<String>();
				CconnectToServer extractData = new CconnectToServer();

				storeExtractedValues = extractData.extractDataFromJson(
						courseIData, "CourseID");
				currentCourseIDd = storeExtractedValues.get(0);

				result[0] = currentCourseIDd;
				currentCourseID= currentCourseIDd;

			}
		}

		/**
		 * Sets the extracted data.
		 *
		 * @param CurrentcourseID the currentcourse id
		 */
		private void SetExtractedData(String CurrentcourseID) {

			currentCourseID= CurrentcourseID;

		}
	}



	/**
	 * Spinner value.
	 */
	private void spinnerValue() {
		// TODO Auto-generated method stub
		spinner1= (Spinner)findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int pos, long id) {
				currentCourseyear = parent.getItemAtPosition(pos).toString();
				Intent i = getIntent();
				display = new GetCourseIdFromserver();
				display.execute(i);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}



	/**
	 * Make create topic button clickable.
	 */
	private void makeCreateTopicButtonClickable(){
		Button toOpenPevForum = (Button) findViewById(R.id.Batchselcted);
		toOpenPevForum.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				spinnerValue();
				//getCourseID(); //get course ID
				launchPrevForumTopicActivity();
			}


		});
	}

	/**
	 * Launch prev forum topic activity.
	 */
	private void launchPrevForumTopicActivity() {
		Intent launchActivityPrevBatchForum = new Intent(CPrevBatchActivity.this, CPreviousBatchForum.class);
		launchActivityPrevBatchForum.putExtra("userID", userID);
		launchActivityPrevBatchForum.putExtra("courseID", courseID);
		launchActivityPrevBatchForum.putExtra("currentcourseID", currentCourseID);
		startActivity(launchActivityPrevBatchForum);

		// TODO Auto-generated method stub

	}


	// add items into spinner dynamically for future
//	public void addItemsOnSpinner2() {
//
//		spinner1 = (Spinner) findViewById(R.id.spinner1);
//		List<String> list = new ArrayList<String>();
//		list.add("2015");
//		list.add("2016");
//		list.add("list 3");
//		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_spinner_item, list);
//		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinner1.setAdapter(dataAdapter);
//	}
//
//	public void addListenerOnSpinnerItemSelection() {
//		spinner1 = (Spinner) findViewById(R.id.spinner1);
//		//	spinner1.setOnItemSelectedListener(new );
//	}



}
