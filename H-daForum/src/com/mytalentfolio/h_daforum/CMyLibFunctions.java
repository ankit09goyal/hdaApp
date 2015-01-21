
package com.mytalentfolio.h_daforum;

import android.content.Context;
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
 * <p><strong> The Class CMyLibFunctions.</strong></br>
 *
 * Class is formed to make general functions that can
 * be reused in the program by the team. It is a
 * customized library for this particular project
 * FUTURE EXTENSION NOTE: the class can be extended to accommodate
 * more functions to remove same functions used in different activities
 * or to add more features in the app </p>
 * 
 * @author Ankit Goyal
 * @version 1.1 
 */
public class CMyLibFunctions {
	
	/**
	 * Show progress.
	 * hides and displays the layouts
	 * or basically swaps between the two layouts
	 *
	 * @param show the show progress layout or not
	 * @param onTrueVisibleLayout the Layout you want to show when the boolean "show" is true
	 * @param onTrueHiddenLayout the Layout you want to hide when the boolean "show" is true
	 */
	public void showProgress(boolean show, LinearLayout onTrueVisibleLayout, LinearLayout onTrueHiddenLayout) {
		if (show) {
			onTrueHiddenLayout.setVisibility(View.GONE);
			onTrueVisibleLayout.setVisibility(View.VISIBLE);
		} else {
			onTrueHiddenLayout.setVisibility(View.VISIBLE);
			onTrueVisibleLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * remove previous views and add a view with the text passed.
	 * used to display and add dynamic text and text views
	 *
	 * @param textToShow the pass the text to be displayed on the screen
	 * @param layoutThatContainsText the layout that contains text.
	 * @param contextActivity the activity's context in which the function is called
	 */
	public void displayResult(String textToShow, LinearLayout layoutThatContainsText, Context contextActivity){
		//remove existing views
		if (((LinearLayout) layoutThatContainsText).getChildCount() > 0) {
			((LinearLayout) layoutThatContainsText).removeAllViews();
		}
		//add new view
		TextView tv = new TextView(contextActivity);
		tv.setText(textToShow);
		tv.setTextSize(15);
		layoutThatContainsText.addView(tv);
	}
}
