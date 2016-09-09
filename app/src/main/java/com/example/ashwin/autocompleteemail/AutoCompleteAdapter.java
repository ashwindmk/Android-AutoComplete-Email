package com.example.ashwin.autocompleteemail;

import java.util.ArrayList;
import java.util.Arrays;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

	Context context;

	private ArrayList<String> fullList;
	private ArrayFilter mFilter;
	private OnPermissionClickListener mListener;

	TextView itemName, itemDel;

	private LayoutInflater mInflater;

	public interface OnPermissionClickListener {
		void onPermissionClicked();
	}

	/** set listener for clicks on the permission request item */
	public void setOnPermissionClickListener(OnPermissionClickListener listener)
	{
		mListener = listener;
	}

	public AutoCompleteAdapter(Context context, String[] list) {
		mInflater   = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fullList = new ArrayList<String>( Arrays.asList(list) );
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return fullList.size();
	}

	@Override
	public String getItem(int position) {
		return fullList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(final int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View rowView =  inflater.inflate(R.layout.adapter_row, parent, false);

		itemName = (TextView) rowView.findViewById(R.id.itemName);
		itemDel = (TextView) rowView.findViewById(R.id.itemDel);

		itemName.setText(getItem(position)+"");

		itemName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onPermissionClicked();
				}
			}
		});

		//clear button click listener
		itemDel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fullList.remove(getItem(position));
				notifyDataSetChanged();
				Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
			}
		});

		return rowView;
	}

	@Override
	public Filter getFilter()
	{
		if (mFilter == null)
		{
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter
	{
		@Override
		protected FilterResults performFiltering(CharSequence prefix)
		{
			FilterResults results = new FilterResults();

			SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(context);

			if( !mSharedPreferencesManager.getCancelledContactsPermission() )
			{
				ArrayList<String> list = new ArrayList<String>(fullList);

				results.values = list;

				results.count = list.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results)
		{
			fullList = (ArrayList<String>) results.values;

			if (results.count > 0)
			{
				notifyDataSetChanged();
			}
			else
			{
				notifyDataSetInvalidated();
			}
		}

	}

}