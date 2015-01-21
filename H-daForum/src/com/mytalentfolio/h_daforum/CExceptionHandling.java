
package com.mytalentfolio.h_daforum;

import android.content.Context;
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
 * <p><strong>The Class CExceptionHandling.</strong></br>
 * Class used for handling the exceptions occurred during the server
 * communication</p>
 * 
 * @author Deepak Patel
 * @version 1.1
 * 
 */
public class CExceptionHandling {
	/** For keeping the track of exception occurred */
	public static ExceptionSet ExceptionState = ExceptionSet.NO_EXCEPTION;

	/** For saving the exception message */
	public static String ExceptionMsg = "";

	/**
	 * Method used for showing the exception message as a Toast.
	 * 
	 * @param mContext
	 *            Context of the application
	 * 
	 */
	public static void showExceptionToast(Context mContext) {

		switch (ExceptionState) {
		case SENT_DATA_UNVERIFIED:
			Toast.makeText(
					mContext,
					"Digital Signature of sent data was not verified\nTry again",
					Toast.LENGTH_LONG).show();
			break;
		case NO_DATA_CONNECTION:
			Toast.makeText(mContext, "No Data Connection\nTry again",
					Toast.LENGTH_LONG).show();
			break;
		case OTHER_EXCEPTIONS:
			Toast.makeText(mContext,
					"Error Occurred\nTry again " + ExceptionMsg,
					Toast.LENGTH_LONG).show();
			break;
		case RECEIVED_DATA_UNVERIFIED:
			Toast.makeText(
					mContext,
					"Digital Signature of received data was not verified\nTry again",
					Toast.LENGTH_LONG).show();
			break;
		case CONNECTION_TIMEOUT:
			Toast.makeText(mContext, "Connection Timeout\nTry again",
					Toast.LENGTH_LONG).show();
			break;
		}
		// Set exception state to no exception
		ExceptionState = ExceptionSet.NO_EXCEPTION;

		// Set exception message to blank
		ExceptionMsg = "";

	}
}
