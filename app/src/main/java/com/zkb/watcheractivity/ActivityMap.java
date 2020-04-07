package com.zkb.watcheractivity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityMap {
    private static ActivityMap activityMap;

    private ActivityMap() {

    }

    public static ActivityMap get() {

        if (activityMap == null) {
            activityMap = new ActivityMap();
        }

        return activityMap;
    }

    private static List<Activity> list = new ArrayList<>();

    public void add(String key, Activity activity) {
        list.add(activity);
    }

    public int getSize() {
        return list.size();
    }
    public List<Activity> getList() {
        return list;
    }


}
