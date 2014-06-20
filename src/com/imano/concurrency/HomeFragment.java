package com.imano.concurrency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class HomeFragment extends Fragment {

	private final String HEADLINES_FEED_URL = "https://s3-eu-west-1.amazonaws.com/imano.test/headlines.json";
	private final String PROFILE_FEED_URL = "https://s3-eu-west-1.amazonaws.com/imano.test/Android/walpapers.json";

	public static final String LOG_TAG = "AndroidTest";

	protected static final int DOWNLOAD_LIST_DATA = 1;

	protected static final int DOWNLOAD_HEADLINES = 2;

	private ListView mListView;

	private TextView mTextView;

	private PlayesListAdapter mAdapter;

	private Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);

		this.mActivity = getActivity();

		mTextView = (TextView) rootView.findViewById(R.id.headline_tv);
		// Set focus to the TextView
		mTextView.setSelected(true);

		mListView = (ListView) rootView.findViewById(R.id.playersList);
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
				
				LinearLayout layout = (LinearLayout) view.findViewById(R.id.button_container);
				layout.setVisibility(View.VISIBLE);
				
				return true;
			}
		});

		mAdapter = new PlayesListAdapter(getActivity(), null);
		mListView.setAdapter(mAdapter);

		new DownloadListDataTask(DOWNLOAD_LIST_DATA).execute(PROFILE_FEED_URL);

		return rootView;
	}

	private class DownloadListDataTask extends AsyncTask<String, Integer, ArrayList<ListObj>> {

		int what = 0;

		public DownloadListDataTask(int what) {
			this.what = what;
		}

		@Override
		protected void onPreExecute() {
			mActivity.setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}

		@Override
		protected void onPostExecute(ArrayList<ListObj> profilesList) {

			Log.e(LOG_TAG, "Download Profile Complete!! ");

			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = profilesList;
			mHandler.sendMessage(msg);

		}

		@Override
		protected ArrayList<ListObj> doInBackground(String... params) {
			String url = params[0];

			ArrayList<ListObj> listData = null;

			// getting JSON string from URL
			JSONObject jsonResponse = getJSONFromUrl(url);
			if (null != jsonResponse) {
				JSONArray imagesListJson = jsonResponse.optJSONArray("images");

				// initialize list
				if (null != imagesListJson && imagesListJson.length() > 0) {
					listData = new ArrayList<>(imagesListJson.length());
				}

				ListObj listObj = new ListObj();

				// adding url's
				for (int i = 0; i < imagesListJson.length(); i++) {

					JSONObject playerJson = imagesListJson.optJSONObject(i);

					listObj.setUrl(playerJson.optString("url"));
					listData.add(listObj);
				}

				return listData;
			} else {
				Log.e(LOG_TAG, "Download Failed!! ");
			}

			return null;
		}
	}

	private class DownloadHeadlinesTask extends AsyncTask<String, Integer, String> {

		int what = 0;

		public DownloadHeadlinesTask(int what) {
			this.what = what;
		}

		@Override
		protected void onPreExecute() {
			mActivity.setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(String headlineText) {

			Log.e(LOG_TAG, "Download Headlines Complete!! ");

			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = headlineText;
			mHandler.sendMessage(msg);
		}

		@Override
		protected String doInBackground(String... params) {
			String url = params[0];

			// getting JSON string from URL
			JSONObject jsonResponse = getJSONFromUrl(url);
			if (null != jsonResponse) {
				JSONArray headlinesListJson = jsonResponse.optJSONArray("headlines");

				StringBuffer headlineText = new StringBuffer();

				// adding url's
				for (int i = 0; i < headlinesListJson.length(); i++) {
					JSONObject headlineJson = headlinesListJson.optJSONObject(i);
					headlineText.append(headlineJson.optString("headline"));
				}

				return headlineText.toString();
			} else {
				Log.e(LOG_TAG, "Download Failed!! ");
			}

			return null;
		}
	}

	public synchronized JSONObject getJSONFromUrl(String url) {
		InputStream is = null;
		JSONObject jsonObject = null;
		String json = null;

		// Making HTTP request
		try {
			// DefaultHttpClient
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpPost = new HttpGet(url);

			HttpResponse response = client.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();
			is = httpEntity.getContent();

			final BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			final StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "n");
			}
			is.close();
			json = sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			if (null != json) {
				jsonObject = new JSONObject(json);
			}

		} catch (JSONException e) {
			Log.e(LOG_TAG, "Error parsing data " + e.toString());
		}

		// return JSON String
		return jsonObject;

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case DOWNLOAD_LIST_DATA:
				mActivity.setProgressBarIndeterminateVisibility(false);

				@SuppressWarnings("unchecked")
				ArrayList<ListObj> profilesList = (ArrayList<ListObj>) msg.obj;
				// updating user interface
				if (null != profilesList && profilesList.size() > 0) {
					mAdapter.setData(profilesList);
					mAdapter.notifyDataSetChanged();
				}

				// start headlines download
				new DownloadHeadlinesTask(DOWNLOAD_HEADLINES).execute(HEADLINES_FEED_URL);
				break;
			case DOWNLOAD_HEADLINES:
				mActivity.setProgressBarIndeterminateVisibility(false);

				// updating user interface
				final String headlineText = (String) msg.obj;
				if (!TextUtils.isEmpty(headlineText)) {
					mTextView.setText(headlineText);
				}
				break;
			}
		}
	};

}