
package com.mytalentfolio.h_daforum;


import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
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
 * <p><strong>The Class CforumDetailOldBatches.</strong></br>
 * This class is to show the old batch topic details
 * </p>
 * 
 * @author Zeeshan Haider
 * @version 1.1
 */
public class CforumDetailOldBatches  extends Activity {

	/** The comment view. */
	EditText commentView;
	
	/** The comment data. */
	String forumTopicID = "", userID = "", commentData = "";
	
	/** The display. */
	displayContent display;
	// displayComment comment;
	/** The context. */
	Context context;
	
	/** The my activity. */
	Activity myActivity;
	
	/** The extract comments. */
	Thread extractComments;
	
	/** The comment progress display. */
	LinearLayout commentDisplay, commentProgressDisplay;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forum_detail_old_batches);

		commentDisplay = (LinearLayout) findViewById(R.id.PFBcommentViewPostLinearLayout);
		commentProgressDisplay = (LinearLayout) findViewById(R.id.PFBprogressOfComments);
		myActivity = this;

		Intent i = getIntent();
		display = new displayContent();
		display.execute(i);

		context = this;

		showProgress(true);

		commentRunnable displayComments = new commentRunnable();
		extractComments = new Thread(displayComments);
		extractComments.start();

	//	makePostCommentClickable();
	}

	/**
	 * Show progress.
	 *
	 * @param show the show
	 */
	public void showProgress(boolean show) {
		if (show) {
			commentDisplay.setVisibility(View.GONE);
			commentProgressDisplay.setVisibility(View.VISIBLE);
		} else {
			commentDisplay.setVisibility(View.VISIBLE);
			commentProgressDisplay.setVisibility(View.GONE);
		}
	}

	/**
	 * The Class displayContent.
	 */
	public class displayContent extends AsyncTask<Intent, Void, String[]> {

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
			displayExtractedData(result[0], result[1]);
		}

		/**
		 * Gets the intent and display data.
		 *
		 * @param i the i
		 * @return the intent and display data
		 */
		private void getIntentAndDisplayData(Intent i) {
			if (!isCancelled()) {
				forumTopicID = i.getStringExtra("topicID");
				userID = i.getStringExtra("userID");
				getDataFromServer(forumTopicID);
			}
		}

		 
		/**
		 * Gets the data from server.
		 *get all data in jason format from server
		 * @param forumTopicID the forum topic id
		 * @return the data from server
		 */
		private void getDataFromServer(String forumTopicID) {
			if (!isCancelled()) {
				String forumData;
				CconnectToServer getData = new CconnectToServer();
				forumData = getData.connect("forumDetail.php", "0",
						forumTopicID);
				extractDataFromJson(forumData);
			}
		}

		
		/**
		 * Extract data from json.
		 *extract data rcvd from server in JSON format
		  return the data extracted
		   extracting forum details
		 * @param forumData the forum data
		 */
		private void extractDataFromJson(String forumData) {
			if (!isCancelled()) {
				String forumHeading = "", forumDescription = "";

				
				Vector<String> storeExtractedValues = new Vector<String>();
				CconnectToServer extractData = new CconnectToServer();

				storeExtractedValues = extractData.extractDataFromJson(
						forumData, "Title");
				forumHeading = storeExtractedValues.get(0);

				storeExtractedValues = extractData.extractDataFromJson(
						forumData, "Description");
				forumDescription = storeExtractedValues.get(0);

				result[0] = forumHeading;
				result[1] = forumDescription;
			}
		}

		/**
		 * Display extracted data.
		 *
		 * @param forumHeading the forum heading
		 * @param forumDescription the forum description
		 */
		private void displayExtractedData(String forumHeading,
				String forumDescription) {
			TextView forumTitle = (TextView) findViewById(R.id.PFBtitle);
			TextView description = (TextView) findViewById(R.id.PFBdescription);
			forumTitle.setText(forumHeading);
			description.setText(forumDescription);
		}
	}

	/**
	 * The Interface commentRunnableInterface.
	 */
	public interface commentRunnableInterface extends Runnable {
		 
		/**
		 * Gets the data from server.
		 *get all the data from server in json format
		 * @return the data from server
		 */
		void getDataFromServer();

		
		/**
		 * Extract data from json.
		 *extract data rcvd from server in JSON format
		  return the data extracted
		 * @param commentData the comment data
		 */
		void extractDataFromJson(String commentData);

		
		/**
		 * Gets the user info from server.
		 *get info from userInfo table from server
		 * @param userID the user id
		 * @return the user info from server
		 */
		String getUserInfoFromServer(String userID);

		
		/**
		 * Extract user name from data.
		 *extract username from the data string from server
		 * @param userInfoDataFromServer the user info data from server
		 * @return the string
		 */
		String extractUserNameFromData(String userInfoDataFromServer);

		//
		/**
		 * Thread msg.
		 *send msg to the handler to display on screen
		 * @param msg the msg
		 */
		void threadMsg(String[][] msg);
	}

	/**
	 * The Class commentRunnable.
	 */
	public class commentRunnable implements commentRunnableInterface {

		/** The result. */
		String[][] result = null;

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			getDataFromServer();
		}

		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.CforumDetailOldBatches.commentRunnableInterface#getDataFromServer()
		 */
		@Override
		// get all the data from server in json format
		public void getDataFromServer() {
			CconnectToServer getData = new CconnectToServer();
			commentData = getData.connect("forumDetail.php", "1", forumTopicID);
			extractDataFromJson(commentData);
		}

		
		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.CforumDetailOldBatches.commentRunnableInterface#extractDataFromJson(java.lang.String)
		 * extract data rcvd from server in JSON format
		   return the data extracted
		 */
		@Override
		public void extractDataFromJson(String commentData) {
			String userID = "";
			String[] comments = null;
			String[] userNameComment = null;
			// extracting comments and username
			try {
				JSONArray json2 = new JSONArray(commentData);
				comments = new String[json2.length()];
				userNameComment = new String[json2.length()];
				result = new String[2][json2.length()];
				result[0] = null;
				result[1] = null;
				for (int i = 0; i < json2.length(); i++) {

					JSONObject obj2 = json2.getJSONObject(i);
					comments[i] = obj2.getString("Comment");
					userID = obj2.getString("UserID");

					String userInfoDataFromServer = getUserInfoFromServer(userID);
					userNameComment[i] = extractUserNameFromData(userInfoDataFromServer);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (commentData.equals("\nnull\n")) {
				comments = new String[1];
				userNameComment = new String[1];
				result = new String[2][1];
				comments[0] = "zero";
				userNameComment[0] = "zero";
				result[0] = comments;
				result[1] = userNameComment;
				threadMsg(result);
			} else {
				result[0] = comments;
				result[1] = userNameComment;
				threadMsg(result);
			}

		}

		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.CforumDetailOldBatches.commentRunnableInterface#threadMsg(java.lang.String[][])
		 */
		@Override
		public void threadMsg(String[][] msg) {
			if (!msg[0][0].equals(null) && !msg[0][0].equals("")) {
				Message msgObj = handleComment.obtainMessage();
				Bundle b = new Bundle();
				b.putStringArray("comments", msg[0]);
				b.putStringArray("userNameComment", msg[1]);
				msgObj.setData(b);
				handleComment.sendMessage(msgObj);
			}
		}

		/** The handle comment. */
		private final Handler handleComment = new Handler() {

			public void handleMessage(Message msg) {
				String[] comments = msg.getData().getStringArray("comments");
				String[] userNameComments = msg.getData().getStringArray(
						"userNameComment");
				displayExtractedData(comments, userNameComments);
			}

			// display the data extracted
			public void displayExtractedData(String[] comments,
					String[] userNameComment) {
				LinearLayout linearLayout = (LinearLayout) myActivity
						.findViewById(R.id.PFBcommentLinearLayout);

				if (((LinearLayout) linearLayout).getChildCount() > 0) {
					((LinearLayout) linearLayout).removeAllViews();
				}
				// display comments and username
				if (comments[0].equals("zero")) {
					TextView tv = new TextView(context);
					tv.setText("No Comments Yet!");
					tv.setTextColor(Color.parseColor("#767676"));
					tv.setTextSize(12);
					linearLayout.addView(tv);
					showProgress(false);
				} else {
					for (int i = 0; i < comments.length; i++) {
						TextView tvComment = new TextView(context);
						tvComment.setTextColor(Color.parseColor("#767676"));
						tvComment.setText(comments[i]);
						tvComment.setTextSize(12);
						linearLayout.addView(tvComment);


						TextView tvUserName = new TextView(context);
						tvUserName.setTextColor(Color.parseColor("#C0C0C0"));
						tvUserName.setText(userNameComment[i]);
						tvUserName.setTextSize(12);
						tvUserName.setGravity(Gravity.RIGHT);
						linearLayout.addView(tvUserName);
					}
					showProgress(false);
				}
			}
		};

		
		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.CforumDetailOldBatches.commentRunnableInterface#getUserInfoFromServer(java.lang.String)
		 * get info from userInfo table from server
		 */
		@Override
		public String getUserInfoFromServer(String userID) {
			CconnectToServer getData = new CconnectToServer();
			return getData.connect("forumDetail.php", "2", userID);
		}

		
		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.CforumDetailOldBatches.commentRunnableInterface#extractUserNameFromData(java.lang.String)
		 * extract username from the data string from server
		 */
		@Override
		public String extractUserNameFromData(String userInfoDataFromServer) {
			try {
				JSONArray json = new JSONArray(userInfoDataFromServer);
				JSONObject obj = json.getJSONObject(0);
				return obj.getString("username");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ("error");
			}
		}
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.forum_topic_detail, menu);
		return true;
	}


}
