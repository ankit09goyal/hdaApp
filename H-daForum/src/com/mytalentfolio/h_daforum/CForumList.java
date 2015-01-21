
package com.mytalentfolio.h_daforum;

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
 * <p><strong> The Class CForumList.</strong></br>
 *
 * the class is used to contain all the information of a 
 * forum topic. This is used for making array list of this class's
 * object and using it to display in List View
 * 
 * FUTURE EXTENSION NOTE: the class can be used to add more variables 
 * in future and implement functions related to this data</p>
 * 
 * @author Ankit Goyal
 * @version 1.1
 */
public class CForumList {
	
	/** The heading of topic, description and username, who has 
	 * posted the topic. */
	String m_heading, m_description, m_userName;
	
	/** The number of comments. */
	String m_noOfComments;
	
	/** The date on which the topic was posted. */
	String m_date;
	
	/**
	 * Instantiates a new c forum list.
	 *
	 * @param heading the heading
	 * @param description the description
	 * @param userName the user name
	 * @param noOfComments the no of comments
	 * @param date the date
	 */
	public CForumList(String heading, String description, String userName, String noOfComments, String date){
		m_heading = heading;
		m_description = description;
		m_userName = userName;
		m_noOfComments = noOfComments;
		m_date = date;
	}
}
