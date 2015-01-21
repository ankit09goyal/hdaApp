
package com.mytalentfolio.h_daforum;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * <p><strong> The Class CPostForumTopic.</strong></br>
 * In this activity user can post a new topic </p>
 *
 * 
 * @author Ankit Goyal
 * @version 1.1 
 */
public class CPostForumTopic extends Activity {

	/** The forum title and forum description edit text view. */
	EditText forumTitleView, forumDescView;
	
	/** The intent of the activity. */
	Intent myIntent;
	
	/** The course id and user id. */
	String userID, courseID;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cpost_forum_topic);
		myIntent = getIntent();	
		
		makePostButtonClickable();
	}

	/**
	 * Make post button clickable.
	 */
	private void makePostButtonClickable() {
			Button postTopic = (Button) findViewById(R.id.postTopic);
			postTopic.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					sendDataToServer();
				}
			});
		}

		/**
		 * Send data to server.
		 */
		private void sendDataToServer() {
			forumTitleView = (EditText) findViewById(R.id.forumTitle);
			forumDescView = (EditText) findViewById(R.id.forumDesc);
			
			String forumTitle = forumTitleView.getText().toString();
			String forumDesc = forumDescView.getText().toString();
			
			boolean valid;
			valid = checkValidityOfTileAndDesc(forumTitle, forumDesc);

			if (valid) {
				PostForumTopic postTopic = new PostForumTopic();
				postTopic.execute(forumTitle, forumDesc);
			}
		}

		/**
		 * Check validity of tile and desc.
		 *
		 * @param forumTitle the forum title
		 * @param forumDesc the forum desc
		 * @return true, if successful
		 */
		private boolean checkValidityOfTileAndDesc(String forumTitle, String forumDesc) {
			View focusView = null;
			if (forumTitle.length() < 15 && forumTitle.length() > 140) {
				forumTitleView.setError(getString(R.string.forum_title_error));
				focusView = forumTitleView;
				focusView.requestFocus();
				return false;
			} else if(forumDesc.length() < 30){
				forumDescView.setError(getString(R.string.forum_desc_error));
				focusView = forumDescView;
				focusView.requestFocus();
				return false;
			}
			else{
				return true;	
			}
		}

		/**
		 * The Class PostForumTopic.
		 * asycn task to send and save the title, description, user Id
		 * on the server
		 */
		public class PostForumTopic extends AsyncTask<String, Void, String> {

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
			 */
			@Override
			protected String doInBackground(String... arg0) {
				String forumTopic = (String) arg0[0];
				String forumDesc = (String) arg0[1];
				extractDataFromIntent(myIntent);
				String result = sendDataToServer(forumTopic, forumDesc);
				return result;
			}
			
			/**
			 * Extract user id and course id from intent.
			 *
			 * @param i the activity's intent
			 */
			private void extractDataFromIntent(Intent i) {
					userID = i.getStringExtra("userID");
					courseID = i.getStringExtra("courseID");
			}

			/**
			 * Send data to server.
			 *
			 * @param forumTopic the forum topic
			 * @param forumDesc the forum desc
			 * @return the result received from the server
			 */
			private String sendDataToServer(String forumTopic, String forumDesc){	
				CconnectToServer sendTopicAndDescToServer = new CconnectToServer();
				String result = sendTopicAndDescToServer.connect("postForumTopic.php",
						forumTopic, forumDesc, userID, courseID);
				return result;
			}
			
			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(String result) {
				if(CExceptionHandling.ExceptionState!=ExceptionSet.NO_EXCEPTION){
					CExceptionHandling.showExceptionToast(getApplicationContext());
				}else
				 if(result.equals("[\"1\"]")) {
				//if (result.equals("\n[\"1\"]\n")) {
					Toast.makeText(getApplicationContext(),
							"New forum topic created successfully", Toast.LENGTH_LONG)
							.show();
					launchForumTopicListActivity();
				} else {
					Toast.makeText(getApplicationContext(),
							"Error in creating new forum topic, please try again later", Toast.LENGTH_LONG).show();
				}
			}

			/**
			 * Launch forum topic list activity.
			 */
			private void launchForumTopicListActivity() {
				Intent launchActivityForumList = new Intent(CPostForumTopic.this, MainActivityChat1.class);
				launchActivityForumList.putExtra("userID", userID);
				launchActivityForumList.putExtra("courseID", courseID);
				startActivity(launchActivityForumList);
			}
		}
}
