package com.ccb.arcselect.utils;

import android.content.Context;
import android.widget.Toast;

public class TUtils {

    private static Toast t;

    public static void show(Context c , String s){
        if (t == null)t = Toast.makeText(c,s,Toast.LENGTH_SHORT);
        t.setText(s);
        t.show();
    }
}
