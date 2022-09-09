package com.han.a175_kg;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class data {

    private final static String PREF_NAME = "pref_sharedpreferences_data";
    private static final String NEW_NOTIFY_YN_KEY = "new_notify_yn_key";
    private static final String TIME = "TIME_key";
    private static final String KG = "KG_key";
    private static final String SIZE = "SIZE_key";

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;
    private static Context mContext;

    private static data mInstance;
    public synchronized static data getInstance(Context ctx) {
        mContext = ctx;
        if (mInstance == null) {
            mInstance = new data();
            mSharedPreferences = ctx.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
        }
        return mInstance;
    }

    //
//    /*------ this is for new notify  --------*/
//    public void setNewNotify(String flag){
//        mEditor.putString(NEW_NOTIFY_YN_KEY, flag);
//        mEditor.commit();
//    }
//
//    public String getNewNotify(){
//        return mSharedPreferences.getString(NEW_NOTIFY_YN_KEY, "N");
//    }
//
    /*------ this is for new notify  --------*/
    public void setTime(String flag,int index){
        mEditor.putString(TIME+index , flag);
        mEditor.commit();
    }

    public String getTime(int index) {
        return mSharedPreferences.getString(TIME+index, "N");
    }

    public void setKg(String flag,int index){
        mEditor.putString(KG+index , flag);
        mEditor.commit();
    }

    public String getKg(int index) {
        return mSharedPreferences.getString(KG+index, "N");
    }


    public void setSIZE(int flag){
        mEditor.putInt(SIZE , flag);
        mEditor.commit();
    }

    public int getSIZE() {
        return mSharedPreferences.getInt(SIZE, 0);
    }


}
