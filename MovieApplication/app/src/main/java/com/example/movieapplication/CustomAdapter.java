package com.example.movieapplication;

import android.annotation.SuppressLint;
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


public class CustomAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    ListModel tempValues = null;

    //konstruktor adaptera do listView
    public CustomAdapter(Activity a, ArrayList d, Resources resLocal) {


        activity = a;
        data = d;
        res = resLocal;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    //rozmiar listView
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

    //klasa elementów xmlowych wystepujacych w listView
    public static class ViewHolder {

        TextView item_name;
        TextView item_age;
        ImageView item_img;
    }

    //tworzenie listView
    @SuppressLint({"InflateParams", "SetTextI18n"})
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            //wyglad kazdego elementu z listView
            vi = inflater.inflate(R.layout.list_item, null);

            //inicjalizacja elementów xmlowych wystepujacych w listView
            holder = new ViewHolder();
            holder.item_name = vi.findViewById(R.id.item_name);
            holder.item_age = vi.findViewById(R.id.item_age);
            holder.item_img = vi.findViewById(R.id.image);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (data.size() <= 0) {
            holder.item_name.setText("No Data");
        } else {

            tempValues = null;
            tempValues = (ListModel) data.get(position);

            //ustawienie wartosci poczatkowych elementów
            holder.item_name.setText(tempValues.get_name());
            holder.item_age.setText(String.valueOf(tempValues.get_age()));
            holder.item_img.setImageBitmap(tempValues.get_img());

            //klikniecie elementu
            vi.setOnClickListener(new OnItemClickListener(position));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            HomeActivity act = (HomeActivity) activity;

            //klikniecie elementu
            act.onItemClick(mPosition);
        }
    }
}