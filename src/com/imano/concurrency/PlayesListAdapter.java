package com.imano.concurrency;

import java.util.ArrayList;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

public class PlayesListAdapter extends BaseAdapter {

	private ArrayList<ListObj> listData;
	private Context context;
	private LayoutInflater layoutInflater;

	public PlayesListAdapter(Context context, ArrayList<ListObj> listData) {
		this.context = context;
		this.listData = listData;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return (null != listData ? listData.size() : 0);
	}

	@Override
	public Object getItem(int position) {
		return (null != listData ? listData.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<ListObj> listData) {
		this.listData = listData;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.snippet_list_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.profile_img);
			
			holder.buttonLayout = (LinearLayout) convertView.findViewById(R.id.button_container); 
			
			holder.saveButton = (ImageView) convertView.findViewById(R.id.save_btn);
			holder.saveButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					WallpaperManager myWallpaperManager  = WallpaperManager.getInstance(context);
		            try {
		            	Bitmap bitmap = ((BitmapDrawable)holder.imageView.getDrawable()).getBitmap();
		            	
		                myWallpaperManager.setBitmap(bitmap);
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
			
				}
			});


			holder.cancelButton = (ImageView) convertView.findViewById(R.id.cancel_button);
			holder.cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder.buttonLayout.setVisibility(View.GONE);
				}
			});

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// setting profile photos
		ListObj profile = (ListObj) getItem(position);
		if (null != profile) {
			// set image here
			Picasso.with(context).load(profile.getUrl()).into(holder.imageView);

			Log.e(HomeFragment.LOG_TAG, "Downloading image : " + profile.getUrl());
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView imageView;
		
		LinearLayout buttonLayout;
		ImageView saveButton;
		ImageView cancelButton;
		
	}

}