
package com.mytalentfolio.h_daforum;

import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
 * <p><strong> The Class MainActivityChat1.</strong></br>
 *
 * The activity displays all the forum topics 
 * This is the center from where all other activities are linked
 * and can launched.
 *  </p>
 * 
 *
 * @author Ankit Goyal
 * @version 1.1
 * 

 */
public class MainActivityChat1 extends Activity {

	/** The list. */
	ListView list;
	
	/** The context of activity. */
	Context mContextActivity;
	
	/** The forum topic id list. */
	ArrayList<String> forumTopicIDList;
	
	/** The user id and course id. */
	String userID="", courseID="";
	
	/** The extracted forum list. */
	Thread extractForumList;
	
	/** The my forum list array. */
	ArrayList<CForumList> mMyForumList;
	
	/** The progress of forum topics layout. */
	LinearLayout progressOfForumTopics;
	
	/** The my list. */
	ListView myList;
	
	/** The course year. */
	String courseYear;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity_chat1);
		
		progressOfForumTopics = (LinearLayout) findViewById(R.id.progressOfForumList); // progress bar
		myList = (ListView) findViewById(R.id.chatBody);  //List in Main forum
		showProgress(true); //show system is progressing
		
		mContextActivity = this;
		getIntentData();
		
       //start of thread
		forumListExtract extractForumTopics = new forumListExtract();
		extractForumList = new Thread(extractForumTopics);
		extractForumList.start();
        makeSettingButtonClickable();
		makeCreateTopicButtonClickable();
		makeExchangeKeyButtonClickable();
		makePrevBatchButtpnClickable();
	}

	/**
	 * Make setting button clickable.
	 */
	private void makeSettingButtonClickable() {
		Button prevBatchButton = (Button) findViewById(R.id.settingsButton);
		prevBatchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchsettingActivity();
			}
		});
	}

	/**
	 * Make previous batch button clickable.
	 */
	private void makePrevBatchButtpnClickable() {
		// TODO Auto-generated method stub
		Button prevBatchButton = (Button) findViewById(R.id.prvForum);
		prevBatchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchPrevBatchActivity();
			}
		});
	}

	/**
	 * Show progress layout.
	 *
	 * @param show the show, if true - display progress layout
	 */
	public void showProgress(boolean show) {
		if (show) {
			myList.setVisibility(View.GONE);
			progressOfForumTopics.setVisibility(View.VISIBLE);
		} else {
			myList.setVisibility(View.VISIBLE);
			progressOfForumTopics.setVisibility(View.GONE);
		}
	}
	
	/**
	 * The Interface forumListExtractInterface.
	 * This is extending runnable class to add custom functions
	 */
	public interface forumListExtractInterface extends Runnable{

		/**
		 * Gets the data from server.
		 *
		 * @param courseID the course id
		 */
		public void getDataFromServer(String courseID);

		/**
		 * Extract and display data in list.
		 *
		 * @param data the data recieved from the server
		 */
		public void extractAndDisplayInList(String data);

		
		/**
		 * Extract user name.
		 * query the userInfo table on server and get username
		 * corresponding to a userID
		 * 
		 * @param userInfo the user info
		 * @return the username
		 */
		public String extractUserName(String userInfo);

		/**
		 * Extract nr of comments.
		 * query the comments table on server and get 
		 * nr of comments corresponding to a TopicID
		 * 
		 * @param comments the comments
		 * @return the string
		 */
		public String extractNrOfComments(String comments);

		/**
		 * Extract current batch's year
		 *
		 * @param coureYearData the course year data received from server
		 */
		public void extractCourseYear(String coureYearData);
		
		/**
		 * Send data extracted from the server 
		 * to handler to display on the activity.
		 */
		public void sendDataToHandler();
	}

	/**
	 * The Class forumListExtract.
	 */
	public class forumListExtract implements forumListExtractInterface {


		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			getDataFromServer(courseID);
		}

		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.MainActivityChat1.forumListExtractInterface#getDataFromServer(java.lang.String)
		 */
		@Override
		public void getDataFromServer(String courseID) {
			String data, courseYearData;
			CconnectToServer getData = new CconnectToServer();
			data = getData.connect("forum.php", "0", courseID);
			courseYearData = getData.connect("forum.php", "3", courseID);
			extractCourseYear(courseYearData);
			extractAndDisplayInList(data);
		}

		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.MainActivityChat1.forumListExtractInterface#extractCourseYear(java.lang.String)
		 */
		@Override
		public void extractCourseYear(String courseYearData){
			CconnectToServer getYear = new CconnectToServer();
			Vector<String> yearTempVector = new Vector<String>();
			yearTempVector = getYear.extractDataFromJson(courseYearData, "Batch");
			courseYear = yearTempVector.get(0);
		}
		
		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.MainActivityChat1.forumListExtractInterface#extractAndDisplayInList(java.lang.String)
		 */
		@Override
		public void extractAndDisplayInList(String data) {
			mMyForumList = new ArrayList<CForumList>();
			forumTopicIDList =  new ArrayList<String>();
			String forumHeading = "";
			String forumDescription = ""; 
			String userName = "";
			String datePosted = "";
			String nrOfComments = "";
			String forumTopicID="";

			try {
				JSONArray json = new JSONArray(data);
				for(int i=0;i<json.length(); i++)
				{
					JSONObject obj=json.getJSONObject(i);
					forumHeading=obj.getString("Title");
					forumDescription=obj.getString("Description");
					datePosted=obj.getString("TimeStamp");
					forumTopicID = obj.getString("ForumTopicID");

					//fetching username
					String userID = obj.getString("UserID");
					CconnectToServer getInfo = new CconnectToServer();
					String userInfo = getInfo.connect("forum.php", "1", userID);
					userName = extractUserName(userInfo);

					//fetching nr of comments
					String forumTitleID = obj.getString("ForumTopicID");
					String comments = getInfo.connect("forum.php", "2", forumTitleID);
					String nrOfCommentsTemp = extractNrOfComments(comments);
					if(nrOfCommentsTemp!=null){
						nrOfComments="Replies: "+nrOfCommentsTemp;
					}
					else{
						nrOfComments="No Reply Yet";
					}

					mMyForumList.add(new CForumList(forumHeading, forumDescription, userName, nrOfComments, datePosted));
					forumTopicIDList.add(forumTopicID);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendDataToHandler();
		}

		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.MainActivityChat1.forumListExtractInterface#sendDataToHandler()
		 */
		@Override
		public void sendDataToHandler() {
			Message msgObj = handleForumList.obtainMessage();
			Bundle b = new Bundle();
			b.putString("forumList", "1");
			msgObj.setData(b);
			handleForumList.sendMessage(msgObj);
		}

		/** The handle forum list. */
		@SuppressLint("HandlerLeak")
		private final Handler handleForumList = new Handler() {

			public void handleMessage(Message msg) {
				String message = msg.getData().getString("forumList");
				if(message.equals("1")){
					displayExtractedDataArrayList();
				}
			}

			// display the data extracted
			public void displayExtractedDataArrayList() {

				showProgress(false);
				
				//display batch year
				TextView batchYearTV = (TextView) findViewById(R.id.currentScreenInfo);
				batchYearTV.setText(courseYear);
				
				//display data in listview
				myArrayAdapter myAdapter = new myArrayAdapter(mContextActivity, mMyForumList);
				list = (ListView) findViewById(R.id.chatBody);
				list.setAdapter(myAdapter);

				makeListClickable();
			}


		};

		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.MainActivityChat1.forumListExtractInterface#extractNrOfComments(java.lang.String)
		 */
		public String extractNrOfComments(String comments){
			String nrOfComments;
			int nrofcomments = 0;
			try {
				JSONArray json = new JSONArray(comments);
				nrofcomments = json.length();
				nrOfComments = Integer.toString(nrofcomments);
				return nrOfComments;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.MainActivityChat1.forumListExtractInterface#extractUserName(java.lang.String)
		 */
		public String extractUserName(String userInfo){
			String userName;
			try {
				JSONArray json = new JSONArray(userInfo);
				JSONObject obj=json.getJSONObject(0);
				userName=obj.getString("username");
				return userName;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * Gets the attached intent data.
	 */
	private void getIntentData(){
		Intent i = getIntent();
		courseID = i.getStringExtra("courseID");
		userID = i.getStringExtra("userID");
		//getDataFromServer(courseID);
	}

	/**
	 * Make list clickable.
	 * on click on a list item, launch new activity
	 */
	private void makeListClickable(){
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				launchTopicDetailActivity(position);
			}
		});
	}

	/**
	 * Launch topic detail activity.
	 *
	 * @param position the position in the list where user has clicked
	 */
	private void launchTopicDetailActivity(int position){
		//get the forum topic id from the position of the list item clicked
		Integer forumTitleID = Integer.parseInt(forumTopicIDList.get(position));
		String titleID = Integer.toString(forumTitleID);

		Intent launchActivityForumDetail = new Intent(MainActivityChat1.this, ForumTopicDetail.class);
		launchActivityForumDetail.putExtra("topicID", titleID);
		launchActivityForumDetail.putExtra("userID", userID);
		startActivity(launchActivityForumDetail);
	}

	/**
	 * The Class myArrayAdapter.
	 * Used to set the values received from the server in customized List View
	 */
	public class myArrayAdapter extends ArrayAdapter<CForumList>{
		
		/** The m_context. */
		private Context m_context;
		
		/**
		 * Instantiates a new my array adapter.
		 *
		 * @param context the activity's context
		 * @param myList the my list
		 */
		public myArrayAdapter(Context context, ArrayList<CForumList> myList) {
			super(context, R.layout.list_view, myList);
			m_context = context;
		}

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View converView, ViewGroup parent){
			LayoutInflater myInflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View myIndividualRowOfList = myInflater.inflate(R.layout.list_view, null);
			TextView heading = (TextView) myIndividualRowOfList.findViewById(R.id.heading);
			TextView description = (TextView) myIndividualRowOfList.findViewById(R.id.description);
			TextView postedBy = (TextView) myIndividualRowOfList.findViewById(R.id.postedBy);
			TextView comments = (TextView) myIndividualRowOfList.findViewById(R.id.comments);
			TextView date = (TextView) myIndividualRowOfList.findViewById(R.id.date);
			ImageView userImg = (ImageView) myIndividualRowOfList.findViewById(R.id.userImg);

			heading.setText(getItem(position).m_heading);
			description.setText(getItem(position).m_description);
			postedBy.setText(getItem(position).m_userName);
			comments.setText(getItem(position).m_noOfComments);
			date.setText(getItem(position).m_date);
			userImg.setImageResource(R.drawable.ic_launcher);

			return(myIndividualRowOfList);
		}
	}

	/**
	 * Make create topic button clickable.
	 */
	private void makeCreateTopicButtonClickable(){
		Button postTopic = (Button) findViewById(R.id.createTopic);
		postTopic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchPostForumTopicActivity();
			}
		});
	}
	
	/**
	 * Make exchange key button clickable.
	 */
	private void makeExchangeKeyButtonClickable(){
		Button exchangeKeyButton = (Button) findViewById(R.id.exchangeKeyShow);
		exchangeKeyButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				launchExchangeKeyActivity();
			}
		});
	}

	/**
	 * Launch post forum topic activity. 
	 */
	private void launchPostForumTopicActivity(){
		Intent launchActivityPostForumTopic = new Intent(MainActivityChat1.this, CPostForumTopic.class);
		launchActivityPostForumTopic.putExtra("userID", userID);
		launchActivityPostForumTopic.putExtra("courseID", courseID);
		startActivity(launchActivityPostForumTopic);
	}

	/**
	 * Launch previous batch activity.
	 */
	private void launchPrevBatchActivity(){
		//get the forum topic id from the position of the list item clicked
		Intent launchprevBatch = new Intent(MainActivityChat1.this, CPrevBatchActivity.class);
		launchprevBatch.putExtra("userID", userID);
		launchprevBatch.putExtra("courseID", courseID);
		startActivity(launchprevBatch);
	}
	
	/**
	 * Launch exchange key activity.
	 */
	private void launchExchangeKeyActivity(){
		Intent launchActivityExchangeKey = new Intent(MainActivityChat1.this, CExchangeKey.class);
		launchActivityExchangeKey.putExtra("userID", userID);
		launchActivityExchangeKey.putExtra("courseID", courseID);
		startActivity(launchActivityExchangeKey);
	}
	
	/**
	 * Launch setting activity.
	 */
	private void launchsettingActivity() {
		// TODO Auto-generated method stub
		Intent launchsetting = new Intent(MainActivityChat1.this, CSettings.class);
		launchsetting.putExtra("userID", userID);
		launchsetting.putExtra("courseID", courseID);
		startActivity(launchsetting);
	}
}