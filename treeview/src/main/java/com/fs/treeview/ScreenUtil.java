package com.fs.treeview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by to-explore-future on 2020/4/17
 */
public class ScreenUtil {

    private static int screenWidth = 0;
    private static int screenHeight = 0;


    public static int getWidth(Context context) {
        if (screenWidth != 0) {
            return screenWidth;
        }
        return getMetrics(context).widthPixels;
    }


    public static int getHeight(Context context) {
        if (screenHeight != 0) {
            return screenHeight;
        }
        return getMetrics(context).heightPixels;
    }

    private static DisplayMetrics getMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
//        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }


}
