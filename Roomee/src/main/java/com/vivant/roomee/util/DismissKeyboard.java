package com.vivant.roomee.util;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * this method manages dismiss keyboard actions
 * Created by guangbo on 15/11/13.
 */
public class DismissKeyboard {

    private Activity activity;

    /**
     * constructor
     * @param activity, android activity
     */
    public DismissKeyboard(Activity activity) {
        this.activity = activity;
    }

    /**
     * this method setup the UI layer so it allows parent view to manages the dismiss keyboard action
     * @param view, parent view of the view calls soft keyboard
     */
    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    /**
     * this method manages hide soft keyboard action via InputMethodManager
     * @param activity, android activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


}
