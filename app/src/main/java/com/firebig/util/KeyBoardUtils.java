package com.firebig.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by root on 16-8-18.
 */
public class KeyBoardUtils {

    public static void closeKeybord(EditText paramEditText, Context paramContext) {
        ((InputMethodManager) paramContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
    }

    public static void openKeybord(EditText paramEditText, Context paramContext) {
        InputMethodManager localInputMethodManager = (InputMethodManager) paramContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        localInputMethodManager.showSoftInput(paramEditText, 2);
        localInputMethodManager.toggleSoftInput(2, 1);
    }
}
