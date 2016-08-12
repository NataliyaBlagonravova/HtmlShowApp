package com.nblagonravova.htmlshowapp.preferences;

import android.content.Context;
import android.preference.PreferenceManager;

public class UrlPreferences {
    private static final String PREF_LAST_URL = "last_url";

    public static final String getStoredUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_URL, null);
    }

    public static void setStoredUrl(Context context, String url){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_URL, url)
                .apply();
    }
}
