package com.imano.concurrency;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

public class LoadingProgress {
	private static ProgressDialog mProgressBar;	
	
	private static Handler mProgressHandler = new Handler();

	public static void show(final Activity activity,
			final String message, final OnCancelListener listener) {
		
		try {
			mProgressHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mProgressBar != null && mProgressBar.isShowing()) {
						mProgressBar.dismiss();
					}
					if (activity.isFinishing()) {
						return;
					}
					
					mProgressBar = new ProgressDialog(activity);
					mProgressBar.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
					mProgressBar.setIndeterminate(true);
					mProgressBar.setCanceledOnTouchOutside(false);
					mProgressBar.setCancelable(true);
					mProgressBar.setMessage(message);
					
					
					if (null != listener) {
						mProgressBar.setOnCancelListener(listener);
					}
					
					if(!mProgressBar.isShowing()){
						mProgressBar.show();
					}
					
					mProgressBar.setContentView(R.layout.snippet_progress_bar);
				}
			});
		} catch (Exception e) {
			Log.v(HomeFragment.LOG_TAG, "Error displaying progress bar " + e.getMessage());
		}
	}

	public static void dismiss() {
		if (null != mProgressBar) {
			mProgressHandler.post(new Runnable() {

				@Override
				public void run() {
					if (mProgressBar != null && mProgressBar.isShowing()) {
						mProgressBar.dismiss();
						mProgressBar= null;
					}
				}
			});

		}
	}
}
