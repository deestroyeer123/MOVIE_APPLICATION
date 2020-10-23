package com.example.movieapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// Adapter class inherits from BaseAdapter and implements OnClickListener
public class CustomAdapter extends BaseAdapter implements View.OnClickListener {

    // Declare Variables
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    ListModel tempValues = null;
    int i = 0;

    // CustomAdapter Constructor
    public CustomAdapter(Activity a, ArrayList d, Resources resLocal) {

// Get passed values
        activity = a;
        data = d;
        res = resLocal;

// Layout inflator to call external xml layout ()
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    // What is the size of Passed Arraylist Size
    public int getCount() {

        if (data.size() <= 0) {
            return 1;
        } else {
            return data.size();
        }
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    // Create a holder Class to contain inflated xml file elements
    public static class ViewHolder {

        public TextView item_name;
        public TextView item_age;

    }

    // Depends upon data size called for each row , Create each ListView row
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

// Inflate list_itemem.xml file for each row ( Defined below )
            vi = inflater.inflate(R.layout.list_item, null);

// View Holder Object to contain list_item.xmlml file elements

            holder = new ViewHolder();
            holder.item_name = vi.findViewById(R.id.item_name);
            holder.item_age = vi.findViewById(R.id.item_age);

// Set holder with LayoutInflater
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (data.size() <= 0) {
            holder.item_name.setText("No Data");
        } else {
// Get each Model object from Arraylist
            tempValues = null;
            tempValues = (ListModel) data.get(position);

// Set Model values in Holder elements
            holder.item_name.setText(tempValues.get_name());
            holder.item_age.setText(String.valueOf(tempValues.get_age()));

// Set Item Click Listner for LayoutInflater for each row
            vi.setOnClickListener(new OnItemClickListener(position));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    // Called when Item click in ListView
    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            HomeActivity act = (HomeActivity) activity;

// Call  onItemClick Method inside MainActivity Class
            act.onItemClick(mPosition);
        }
    }
}