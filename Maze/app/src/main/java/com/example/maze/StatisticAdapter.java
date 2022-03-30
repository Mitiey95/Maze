package com.example.maze;

import android.content.Context;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.logging.Logger;

public class StatisticAdapter extends ArrayAdapter<Result> {
    ArrayList<Result> results;
    Context context;

    public StatisticAdapter(Context context, ArrayList<Result> results) {
        super(context, R.layout.result);
        this.results = results;
        this.context = context;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.result, parent, false);
            mViewHolder.text_view1 = (TextView) convertView.findViewById(R.id.text_view1);
            mViewHolder.text_view2 = (TextView) convertView.findViewById(R.id.text_view2);
            mViewHolder.text_view3 = (TextView) convertView.findViewById(R.id.text_view3);
            mViewHolder.text_view4 = (TextView) convertView.findViewById(R.id.text_view4);
            mViewHolder.text_view5 = (TextView) convertView.findViewById(R.id.text_view5);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.text_view1.setText(results.get(position).getDate());
        mViewHolder.text_view2.setText(""+  results.get(position).getRows());
        mViewHolder.text_view3.setText(""+  results.get(position).getColumns());
        mViewHolder.text_view4.setText(""+  results.get(position).getSpeed());
        if(results.get(position).getWatch() >= results.get(position).getRows() * results.get(position).getColumns()){
            mViewHolder.text_view5.setText("all");
        }
        else {
            mViewHolder.text_view5.setText(""+  results.get(position).getWatch());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView text_view1;
        TextView text_view2;
        TextView text_view3;
        TextView text_view4;
        TextView text_view5;
    }

}
