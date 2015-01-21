

package com.mytalentfolio.h_daforum;

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
 * <p><strong> The Class ExceptionSet.</strong></br>
 * Enum: for the types of exceptions that could occur during 
 * communicating with server.
 *  </p>
 * 
 *
 * @author Deepak Patel
 * @version 1.1
 * 
 */
public enum ExceptionSet {
	/**Used, when there is no exception.*/
	NO_EXCEPTION, 
	/**Used, when digital signature of the sent data is unverified*/
	SENT_DATA_UNVERIFIED, 
	/**Used, when digital signature of the received data is unverified*/
	RECEIVED_DATA_UNVERIFIED, 
	/**Used for no data connection*/
	NO_DATA_CONNECTION,
	/**Used for connection timeout to server*/
	CONNECTION_TIMEOUT,
	/**Other exceptions*/
	OTHER_EXCEPTIONS
}
