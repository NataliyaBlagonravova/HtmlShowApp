package com.nblagonravova.htmlshowapp.activities;

import android.support.v4.app.Fragment;

import com.nblagonravova.htmlshowapp.fragments.HtmlShowFragment;

public class HtmlShowActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return HtmlShowFragment.newInstance();
    }
}
