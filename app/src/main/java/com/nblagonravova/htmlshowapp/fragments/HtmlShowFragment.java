package com.nblagonravova.htmlshowapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nblagonravova.htmlshowapp.R;
import com.nblagonravova.htmlshowapp.connections.HtmlLoader;
import com.nblagonravova.htmlshowapp.preferences.UrlPreferences;

import java.io.IOException;



public class HtmlShowFragment extends Fragment{

    private static final String TAG = HtmlShowFragment.class.getSimpleName();
    private static final String SAVED_HTML = "saved_html";

    private String mHtml;

    private TextView mHtmlTextView;
    private SearchView mSearchView;

    public static HtmlShowFragment newInstance(){
        return new HtmlShowFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null){
            mHtml = savedInstanceState.getString(SAVED_HTML);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_html_show, container, false);

        mHtmlTextView = (TextView) view.findViewById(R.id.html_text_view);

        if (savedInstanceState != null){
            mHtmlTextView.setText(mHtml);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_HTML, mHtml);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_html_show, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        mSearchView = (SearchView) searchItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String url) {
                Log.d(TAG, "QueryTextSubmit: " + url);
                UrlPreferences.setStoredUrl(getActivity(), url);
                mSearchView.clearFocus();
                new LoadHtmlTask(url).execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = UrlPreferences.getStoredUrl(getActivity());
                mSearchView.setQuery(url, false);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_clear:
                UrlPreferences.setStoredUrl(getActivity(), null);

                updateHtml("");

                mSearchView.onActionViewCollapsed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateHtml(String html) {
        mHtml = html;
        mHtmlTextView.setText(mHtml);
    }


    private class LoadHtmlTask extends AsyncTask<Void, Void, String>{

        private  final String TAG = LoadHtmlTask.class.getSimpleName();

        private String mUrl;
        boolean mNetworkAvailableAndConnected;

        HtmlLoader mHtmlLoader;

        public LoadHtmlTask(String query) {
            mUrl = query;
            mHtmlLoader = new HtmlLoader(getActivity());
        }

        @Override
        protected void onPreExecute() {
            updateHtml("");

            if (!mHtmlLoader.isNetworkAvailableAndConnected()){
                Log.d(TAG, "Internet error");
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_LONG).show();
                mNetworkAvailableAndConnected = false;
            } else {
                mNetworkAvailableAndConnected = true;
            }
        }

        @Override
        protected String doInBackground(Void... args) {
            String html = "";

            if (mNetworkAvailableAndConnected) {
                try {
                    html = mHtmlLoader.load(mUrl);
                } catch (IOException e) {
                    Log.d(TAG, "Url error", e);
                }
            }
            return html;
        }

        @Override
        protected void onPostExecute(String html) {
            if (mNetworkAvailableAndConnected && html == "") {
                Toast.makeText(getActivity(), R.string.url_error, Toast.LENGTH_LONG).show();

            }else {
                updateHtml(html);
            }
        }
    }
}