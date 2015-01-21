
package com.mytalentfolio.h_daforum;

import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * <p><strong> The Class ForumTopicDetail.</strong></br>
 * The activity displays the detail of the topic 
 * user has clicked on. User can also post comments in
 * this activity
 *  </p>
 * 
 *
 * @author Ankit Goyal
 * @version 1.1
 * 
 *
 */
public class ForumTopicDetail extends Activity {

	/** The comment view. */
	EditText commentView;

	String forumTopicID = "", userID = "", commentData = "";
	
	/** The display content's class object. */
	displayContent display;

	/** The context of activity. */
	Context context;
	
	/** The my activity. */
	Activity myActivity;
	
	/** The extract comments. */
	Thread extractComments;
	
	LinearLayout commentDisplay, commentProgressDisplay;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forum_topic_detail);

		commentDisplay = (LinearLayout) findViewById(R.id.commentViewPostLinearLayout);
		commentProgressDisplay = (LinearLayout) findViewById(R.id.progressOfComments);
		myActivity = this;

		Intent i = getIntent();
		
		//start display async task to fetch and display
		//the details of the forum topic
		display = new displayContent();
		display.execute(i);

		context = this;

		showProgress(true);

		commentRunnable displayComments = new commentRunnable();
		extractComments = new Thread(displayComments);
		extractComments.start();

		makePostCommentClickable();
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
	 * async task to display the forum topic details
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
		 * @param i the intent
		 */
		private void getIntentAndDisplayData(Intent i) {
			if (!isCancelled()) {
				forumTopicID = i.getStringExtra("topicID");
				userID = i.getStringExtra("userID");
				getDataFromServer(forumTopicID);
			}
		}

		/**
		 * get all data in jason format from server
		 *
		 * @param forumTopicID the forum topic id
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
		 * 
		 * extract data rcvd from server in JSON format
		 * return the data extracted
		 *
		 * @param forumData the forum data rcvd from server
		 */
		private void extractDataFromJson(String forumData) {
			if (!isCancelled()) {
				String forumHeading = "", forumDescription = "";

				// extracting forum details
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
			TextView forumTitle = (TextView) findViewById(R.id.title);
			TextView description = (TextView) findViewById(R.id.description);
			forumTitle.setText(forumHeading);
			description.setText(forumDescription);
		}
	}

	/**
	 * The Interface commentRunnableInterface.
	 */
	public interface commentRunnableInterface extends Runnable {
		// get all the data from server in json format
		/**
		 * Gets the data from server.
		 */
		void getDataFromServer();

		// extract data rcvd from server in JSON format
		// return the data extracted
		/**
		 * Extract data from json.
		 *
		 * @param commentData the comment data rcvd from server
		 */
		void extractDataFromJson(String commentData);

		// get info from userInfo table from server
		/**
		 * Gets the user info from server.
		 *
		 * @param userID the user id
		 * @return the user info from server
		 */
		String getUserInfoFromServer(String userID);

		// extract username from the data string from server
		/**
		 * Extract user name from data.
		 *
		 * @param userInfoDataFromServer the user info data from server
		 * @return the string
		 */
		String extractUserNameFromData(String userInfoDataFromServer);

		//send msg to the handler to display on screen
		/**
		 * Thread msg.
		 *
		 * @param msg the msg
		 */
		void threadMsg(String[][] msg);
	}

	/**
	 * The Class commentRunnable.
	 * extracts comment from the server
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
		 * @see com.mytalentfolio.h_daforum.ForumTopicDetail.commentRunnableInterface#getDataFromServer()
		 */
		@Override
		// get all the data from server in json format
		public void getDataFromServer() {
			CconnectToServer getData = new CconnectToServer();
			commentData = getData.connect("forumDetail.php", "1", forumTopicID);
			extractDataFromJson(commentData);
		}

		// extract data rcvd from server in JSON format
		// return the data extracted
		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.ForumTopicDetail.commentRunnableInterface#extractDataFromJson(java.lang.String)
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

			//if (commentData.equals("\nnull\n")) {
			if (commentData.equals("-1")) {
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
		 * @see com.mytalentfolio.h_daforum.ForumTopicDetail.commentRunnableInterface#threadMsg(java.lang.String[][])
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
		@SuppressLint("HandlerLeak")
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
						.findViewById(R.id.commentLinearLayout);

				if (((LinearLayout) linearLayout).getChildCount() > 0) {
					((LinearLayout) linearLayout).removeAllViews();
				}
				// display comments and username
				if (comments[0].equals("zero")) {
					TextView tv = new TextView(context);
					tv.setText("No Comments Yet!");
					tv.setTextSize(12);
					tv.setTextColor(Color.parseColor("#767676"));
					linearLayout.addView(tv);
					showProgress(false);
				} else {
					for (int i = 0; i < comments.length; i++) {
						TextView tvComment = new TextView(context);
//						tvComment.setMaxLines(2000);
//						tvComment.setBackground(getResources().getDrawable(R.drawable.listview));
//						LinearLayout.LayoutParams xxx = new LinearLayout.LayoutParams(420,60);
//						tvComment.setLayoutParams(xxx);
						tvComment.setText(comments[i]);
						tvComment.setTextSize(12);
						tvComment.setTextColor(Color.parseColor("#767676"));
						linearLayout.addView(tvComment);


						TextView tvUserName = new TextView(context);
						tvUserName.setTextColor(Color.parseColor("#C0C0C0"));
						//tvUserName.setTextColor(Color.RED);
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
		 * @see com.mytalentfolio.h_daforum.ForumTopicDetail.commentRunnableInterface#getUserInfoFromServer(java.lang.String)
		 */
		@Override
		public String getUserInfoFromServer(String userID) {
			CconnectToServer getData = new CconnectToServer();
			return getData.connect("forumDetail.php", "2", userID);
		}

		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.ForumTopicDetail.commentRunnableInterface#extractUserNameFromData(java.lang.String)
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

	/**
	 * Make post comment clickable.
	 */
	private void makePostCommentClickable() {
		Button postComment = (Button) findViewById(R.id.postComment);
		postComment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendDataToServer();
			}
		});
	}

	/**
	 * Send data (comment) to server.
	 */
	private void sendDataToServer() {
		commentView = (EditText) findViewById(R.id.editComment);
		String commentToBePosted = commentView.getText().toString();

		boolean valid;
		valid = checkValidityOfComment(commentToBePosted);

		if (valid) {
			// Intent intent = getIntent();
			PostCommentTask postComment = new PostCommentTask();
			postComment.execute(commentToBePosted);
		}
	}

	/**
	 * Check validity of comment.
	 *
	 * @param commentToBePosted the comment to be posted
	 * @return true, if successful
	 */
	private boolean checkValidityOfComment(String commentToBePosted) {
		View focusView = null;
		if (commentToBePosted.length() < 5) {
			commentView.setError(getString(R.string.comment_error));
			focusView = commentView;
			focusView.requestFocus();
			return false;
		} else {
			return true;
		}
	}

	/**
	 * The Class PostCommentTask.
	 * asncy task to send comment to the server
	 */
	public class PostCommentTask extends AsyncTask<String, Void, String> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String doInBackground(String... arg0) {
			String comment = (String) arg0[0];
			CconnectToServer sendCommentToServer = new CconnectToServer();
			String result = sendCommentToServer.connect("postComment.php",
					comment, forumTopicID, userID);

			return result;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			 if(result.equals("[\"1\"]")) {
			//if (result.equals("\n[\"1\"]\n")) {
				Toast.makeText(getApplicationContext(),
						"Comment posted successfully", Toast.LENGTH_LONG)
						.show();
				refreshComments();
			} else {
				Toast.makeText(getApplicationContext(),
						"Error in posting comment", Toast.LENGTH_LONG).show();
			}
		}

		/**
		 * Refresh comments.
		 */
		private void refreshComments() {
			// Intent intent = getIntent();
			// finish();
			// startActivity(intent);

			commentRunnable displayComments = new commentRunnable();
			extractComments = new Thread(displayComments);
			extractComments.start();
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
