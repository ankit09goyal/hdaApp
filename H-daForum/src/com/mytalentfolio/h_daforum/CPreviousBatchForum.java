
package com.mytalentfolio.h_daforum;

import java.util.ArrayList;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


//TODO: Auto-generated Javadoc
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
* <p><strong> The Class CPreviousBatchForum.</strong></br>
* this class shows the topics from the batch that has been selected
*  </p>
*
* 
* @author Zeeshan Haider
* @version 1.1 
*/
public class CPreviousBatchForum extends Activity {
	
	/** The progress of forum topics. */
	LinearLayout progressOfForumTopics;
	
	/** The my list. */
	ListView myList;
	
	/** The m context activity. */
	Context mContextActivity;
	
	/** The current course, user id. */
	String userID="", courseID="", currentCourse="";
	
	/** The forum topic id list. */
	ArrayList<String> forumTopicIDList;
	
	/** The my forum list. */
	ArrayList<CForumList> mMyForumList;
	
	/** The extract forum list. */
	Thread extractForumList;
	
	/** The list. */
	ListView list;
	
	/** The course year. */
	String courseYear;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cprevious_batch_forum);
		progressOfForumTopics = (LinearLayout) findViewById(R.id.PBprogressOfForumList); // progress bar
		myList = (ListView) findViewById(R.id.PBchatBody);  //List in Main forum

		showProgress(true); //show system is progressing
		mContextActivity = this;
		getIntentData();

		//start of thread
		forumListExtract extractForumTopics = new forumListExtract();
		extractForumList = new Thread(extractForumTopics);
		extractForumList.start();

	}

	/**
	 * Show progress.
	 *
	 * @param show if param show is true
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
	 */
	public interface forumListExtractInterface extends Runnable{

		/**
		 * Gets the data from server.
		 *
		 * @param courseID the course id
		 * @return the data from server
		 */
		public void getDataFromServer(String courseID);

		/**
		 * Extract and display in list.
		 *
		 * @param data the data
		 */
		public void extractAndDisplayInList(String data);

		
		/**
		 * Extract user name.
		 *query the userInfo table on server and gets username
		 *corresponding to a userID
		 * @param userInfo the user info
		 * @return the string
		 */
		public String extractUserName(String userInfo);


		/**
		 *query the comments table on server and get 
		 *number of comments corresponding to a TopicID
		 * @param comments the comments
		 * @return the string
		 */
		public String extractNrOfComments(String comments);

		//extract current batch's year
		/**
		 * Extract course year.
		 *
		 * @param coureYearData the course year data
		 */
		public void extractCourseYear(String coureYearData);

		/**
		 * Send data to handler.
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
			getDataFromServer(currentCourse);
		}

		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.CPreviousBatchForum.forumListExtractInterface#getDataFromServer(java.lang.String)
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
		 * @see com.mytalentfolio.h_daforum.CPreviousBatchForum.forumListExtractInterface#extractCourseYear(java.lang.String)
		 */
		@Override
		public void extractCourseYear(String courseYearData){
			CconnectToServer getYear = new CconnectToServer();
			Vector<String> yearTempVector = new Vector<String>();
			yearTempVector = getYear.extractDataFromJson(courseYearData, "Batch");
			courseYear = yearTempVector.get(0);
		}
		
		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.CPreviousBatchForum.forumListExtractInterface#extractAndDisplayInList(java.lang.String)
		 * extracting data from jason format
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
		 * @see com.mytalentfolio.h_daforum.CPreviousBatchForum.forumListExtractInterface#sendDataToHandler()
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
				TextView batchYearTV = (TextView) findViewById(R.id.PBcurrentScreenInfo);
				batchYearTV.setText(courseYear);
				
				//display data in listview
				myArrayAdapter myAdapter = new myArrayAdapter(mContextActivity, mMyForumList);
				list = (ListView) findViewById(R.id.PBchatBody);
				list.setAdapter(myAdapter);
				makeListClickable();
			}


		};

		
		/* (non-Javadoc)
		 * @see com.mytalentfolio.h_daforum.CPreviousBatchForum.forumListExtractInterface#extractNrOfComments(java.lang.String)
		 * query the comments table on server and get 
		   nr of comments corresponding to a TopicID
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
		 * @see com.mytalentfolio.h_daforum.CPreviousBatchForum.forumListExtractInterface#extractUserName(java.lang.String)
		 * query the userInfo table on server and gets username
		   corresponding to a userID
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
	 * Gets the intent data.
	 *
	 * @return the intent data
	 */
	private void getIntentData(){
		Intent i = getIntent();
		courseID = i.getStringExtra("courseID");
		userID = i.getStringExtra("userID");
		currentCourse = i.getStringExtra("currentcourseID");
		//getDataFromServer(courseID);
	}
	
	/**
	 * Make list clickable.
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
	 * The Class myArrayAdapter.
	 */
	public class myArrayAdapter extends ArrayAdapter<CForumList>{
		
		/** The m_context. */
		private Context m_context;
		
		/**
		 * Instantiates a new my array adapter.
		 *
		 * @param context the context
		 * @param myList the my list
		 */
		public myArrayAdapter(Context context, ArrayList<CForumList> myList) {
			super(context, R.layout.list_view, myList);
			m_context = context;
		}

		//setting values in each row of listview
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
	 * Launch topic detail activity.
	 *get the forum topic id from the position of the list item clicked
	 * @param position for title id extraction
	 */
	private void launchTopicDetailActivity(int position){
	
		Integer forumTitleID = Integer.parseInt(forumTopicIDList.get(position));
		String titleID = Integer.toString(forumTitleID);

		Intent launchActivityForumDetail = new Intent(CPreviousBatchForum.this, CforumDetailOldBatches.class);
		launchActivityForumDetail.putExtra("topicID", titleID);
		launchActivityForumDetail.putExtra("userID", userID);
		startActivity(launchActivityForumDetail);
	}
}
