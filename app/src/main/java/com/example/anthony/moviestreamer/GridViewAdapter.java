package com.example.anthony.moviestreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anthony.moviestreamer.R;
import com.squareup.picasso.Picasso;

public class GridViewAdapter extends ArrayAdapter<resultData> {

    Context context;
    int layoutResId;
    resultData data[] = null;

    public GridViewAdapter(Context context, int layoutResId, resultData[] data) {
        super(context, layoutResId, data);
        this.layoutResId = layoutResId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        movieHolder holder = null;

        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);

            holder = new movieHolder();
            holder.imageIcon = (ImageView)convertView.findViewById(R.id.image);
            holder.textTitle = (TextView)convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        }
        else
        {
            holder = (movieHolder)convertView.getTag();
        }

        resultData movie = data[position];
        holder.textTitle.setText(movie.title);
        Picasso.with(this.context).load(movie.icon).into(holder.imageIcon);


        return convertView;
    }

    static class movieHolder
    {
        ImageView imageIcon;
        TextView textTitle;
    }
}