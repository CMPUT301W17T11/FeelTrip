package com.example.henzoshimada.feeltrip;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.henzoshimada.feeltrip.Mood;

import java.util.ArrayList;

public class RequestAdapter extends ArrayAdapter<FollowRequest> {

    //private ArrayList<Mood> dataSet;
    //Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView userName;
        ImageButton cancelButton;
        ImageButton acceptButton;
    }

    public RequestAdapter(ArrayList<FollowRequest> RequestArrayList, Context context) {
        super(context, R.layout.request_list_item, RequestArrayList);
        //this.dataSet = moodArrayList;
        //this.mContext=context;
    }


    //private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FollowRequest followRequest = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.request_list_item, parent, false);

            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.acceptButton = (ImageButton) convertView.findViewById(R.id.accept_request);
            viewHolder.cancelButton = (ImageButton) convertView.findViewById(R.id.cancel_request);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.userName.setText(followRequest.getSender());

        viewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("requestTag","accept follow request");
                FeelTripApplication.getRequestsArray().remove(position);
                FeelTripApplication.getRequestAdapter(getContext()).notifyDataSetChanged();
            }
        });

        viewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("requestTag","cancel follow request");
                FeelTripApplication.getRequestsArray().remove(position);
                FeelTripApplication.getRequestAdapter(getContext()).notifyDataSetChanged();
            }
        });

        return convertView;
    }
}